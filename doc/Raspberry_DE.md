# Raspberry Pi
Der *Smart Appliance Enabler* benötigt einen [Raspberry Pi](https://de.wikipedia.org/wiki/Raspberry_Pi) als Hardware. Dieser extrem preiswerte Kleinstcomputer (ca. 40 Euro) ist perfekt zum Steuern und Messen geeignet, da er bereits [digitale Ein-/Ausgabe-Schnittstellen](https://de.wikipedia.org/wiki/Raspberry_Pi#GPIO) enthält, die zum Schalten sowie zum Messen des Stromverbrauchs benötigt werden. Hinsichtlich der Leistung empfiehlt es sich, einen **Raspberry Pi 2 Model B** oder neuer zu verwenden.

Für den Raspberry Pi existieren verschiedene, darauf zugeschnittene, Betriebsysteme (Images), wobei  [Raspbian Jessie](https://www.raspberrypi.org/downloads/raspbian) verwendet werden sollte, da dieses bereits die vom *Smart Appliance Enabler* benötigte Java-Runtime beinhaltet ([Installationsanleitung](http://www.pc-magazin.de/ratgeber/raspberry-pi-raspbian-einrichten-installieren-windows-mac-linux-anleitung-tutorial-2468744.html)).

An die GPIO-Pins des Raspberry können diverse Schalter und/oder Stromzähler angeschlossen werden, d.h. ein einziger Raspberry Pi kann eine Vielzahl von Geräten verwalten. Dabei darf jedoch die **Stromstärke** am 5V-Pin den Wert von 300 mA (Model B) bzw. 500mA (Model A) und am 3,3V-Pin den Wert von 50mA nicht überschreiten ([Quelle](http://elinux.org/RPi_Low-level_peripherals#General_Purpose_Input.2FOutput_.28GPIO.29))!

Die Nummerierung der Pins richtet sich nach [Pi4J](http://pi4j.com/images/gpio-control-example-large.png) und weicht von der offiziellen Nummerierung ab!

TODO Bauanleitung mit Hutschienenhalter hinzufügen