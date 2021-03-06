/*
 * Copyright (C) 2018 Axel Müller <axel.mueller@avanux.de>
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

package de.avanux.smartapplianceenabler.modbus.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadFloatHoldingRegisterExecutorImpl extends ReadHoldingRegisterExecutor<Float>
        implements ReadFloatHoldingRegisterExecutor {

    private Logger logger = LoggerFactory.getLogger(ReadFloatHoldingRegisterExecutorImpl.class);
    private Float factorToValue;

    public ReadFloatHoldingRegisterExecutorImpl(String address, int bytes, Float factorToValue) {
        super(address, bytes);
        this.factorToValue = factorToValue;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public Float getValue() {
        Integer[] byteValues = getByteValues();
        if(getBytes() == 2) {
            return Float.intBitsToFloat(byteValues[0] << 16 | byteValues[1]) * getInitializedFactorToValue();
        } else if(getBytes() == 4) {
            return Float.intBitsToFloat(byteValues[0] << 48 | byteValues[1] << 32 | byteValues[2] << 16 | byteValues[3]) * getInitializedFactorToValue();
        }
        logger.error("{}: Float has to be composed of 2 or 4 bytes!", getApplianceId());
        return null;
    }

    private Float getInitializedFactorToValue() {
        if(this.factorToValue == null) {
            return 1.0f;
        }
        return this.factorToValue;
    }
}
