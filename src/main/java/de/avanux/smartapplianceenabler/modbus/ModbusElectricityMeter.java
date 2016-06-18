/*
 * Copyright (C) 2016 Axel Müller <axel.mueller@avanux.de>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package de.avanux.smartapplianceenabler.modbus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import de.avanux.smartapplianceenabler.appliance.ApplianceIdConsumer;
import de.avanux.smartapplianceenabler.log.ApplianceLogger;
import org.slf4j.LoggerFactory;

import de.avanux.smartapplianceenabler.appliance.Meter;

/**
 * Represents a ModBus electricity meter device accessible by ModBus TCP.
 * The device is polled according to poll interval in order to provide min/avg/max values of the measurement interval.
 * The TCP connection to the device remains established across the polls.
 */
public class ModbusElectricityMeter extends ModbusSlave implements Meter, ApplianceIdConsumer {

    @XmlTransient
    private ApplianceLogger logger = new ApplianceLogger(LoggerFactory.getLogger(ModbusElectricityMeter.class));
    @XmlAttribute
    private String registerAddress;
    @XmlAttribute
    private Integer pollInterval = 10; // seconds
    @XmlAttribute
    private Integer measurementInterval; // seconds
    @XmlTransient
    private Map<Long,Float> timestampWithPower = new HashMap<Long,Float>();

    @Override
    public void setApplianceId(String applianceId) {
        super.setApplianceId(applianceId);
        this.logger.setApplianceId(applianceId);
    }

    @Override
    public int getAveragePower() {
        double sum = 0.0f;
        if(!timestampWithPower.isEmpty()) {
          for (Float value : timestampWithPower.values()) {
              sum += value;
          }
          return Double.valueOf(sum / timestampWithPower.size()).intValue();
        }
        return 0;
    }

    @Override
    public int getMinPower() {
        float minValue = Float.MAX_VALUE;
        if(!timestampWithPower.isEmpty()) {
            for (Float value : timestampWithPower.values()) {
                if(value < minValue) {
                    minValue = value;
                }
            }
            return Float.valueOf(minValue).intValue();
        }
        return 0;
    }

    @Override
    public int getMaxPower() {
        float maxValue = Float.MIN_VALUE;
        if(!timestampWithPower.isEmpty()) {
            for (Float value : timestampWithPower.values()) {
                if(value > maxValue) {
                    maxValue = value;
                }
            }
            return Float.valueOf(maxValue).intValue();
        }
        return 0;
    }

    @Override
    public boolean isOn() {
        return getPower() > 0;
    }

    @Override
    public Integer getMeasurementInterval() {
        return measurementInterval;
    }

    @Override
    public void start(Timer timer) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                addValue(getPower());
            }
        }, 0, pollInterval * 1000);
    }
    
    private float getPower() {
        try {
            ReadInputRegisterExecutor executor = new ReadInputRegisterExecutor(registerAddress);
            executor.setApplianceId(getApplianceId());
            executeTransaction(executor, true);
            Float registerValue = executor.getRegisterValue();
            if(registerValue != null) {
                return registerValue;
            }
        }
        catch(Exception e) {
            logger.error("Error reading input register " + registerAddress, e);
        }
        return 0;
    }

    private void addValue(float power) {
        long currentTimestamp = System.currentTimeMillis();
        // remove expired values
        Set<Long> expiredTimestamps = new HashSet<Long>();
        for(Long cachedTimeStamp : timestampWithPower.keySet()) {
            if(cachedTimeStamp < currentTimestamp - measurementInterval * 1000) {
                expiredTimestamps.add(cachedTimeStamp);
            }
        }
        for(Long expiredTimestamp : expiredTimestamps) {
            timestampWithPower.remove(expiredTimestamp);
        }
        // add new value
        timestampWithPower.put(currentTimestamp, power);
        logger.debug("timestamps added/removed/total: 1/" + expiredTimestamps.size() + "/" + timestampWithPower.size());
    }
}