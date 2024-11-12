# qc baseband stuff

## mobile stuff / deploying core network + eNodeB

[slides on LTE / attack surface](https://comsecuris.com/slides/lte_4get_about_it.pdf)
[nickvsnetworking](https://nickvsnetworking.com)

* srsran eNodeB + Open5GS + kamalio : [](https://github.com/herlesupreeth/docker_open5gs)
* srsran eNodeB + corenet (python core network) : [](https://github.com/mitshell/corenet)

## general stuff / architecture 

[QDSP 6 architecture](http://arith21.arithsymposium.org/presentations/pres_BoF6.pdf)
[hexagon presentation at defcon](https://media.defcon.org/DEF%20CON%2026/DEF%20CON%2026%20presentations/DEFCON-26-Seamus-Burke-Journey-Into-Hexagon.pdf)

## decompile

* download radio.img for pixel devices on google [website](https://developers.google.com/android/images)
* use qc_image_unpacker to unpack image file
* mount the modem image file using `mount -r modem /mnt/`
* follow the instructions of qc_baseband_scripts repo to assemble modem.bin files

* use IDA 8.x on debian
* `git clone https://github.com/gsmk/hexagon`, install cmake/build-essentials then `make`
* load modem.bin in IDA 

TODO:
* IDAPython script to parse msg_hash.txt

## debug

[old DIAG implem](https://fahrplan.events.ccc.de/congress/2011/Fahrplan/attachments/2022_11-ccc-qcombbdbg.pdf)
[recent study of DIAG](https://alisa.sh/slides/AdvancedHexagonDiag.pdf)

### exposing DIAG over USB

* execute the following stuff on a rooted phone (works with pixel 4)

```
resetprop ro.bootmode usbradio
resetprop ro.build.type userdebug
setprop sys.usb.config diag,diag_mdm,adb
diag_mdlog
```

* apply the [following](https://lkml.org/lkml/2021/8/16/228) kernel patch to recognize the usb serial interface

### logging

basic logging from RIL: `adb logcat -b radio`
to get QC proprietary logging from hexagon :

* expose DIAG over USB
* use [this](https://github.com/gregjhogan/qc_debug_monitor) implementation
* use msg_hash.txt provided in the extracted firmware image from Google (see above)

### nvram fs (efs) access

* expose DIAG over USB
* use [QCSuper](https://github.com/P1sec/QCSuper) with `--efs-shell` and `--usb-modem`

### using AT commands interface via serial

```
sunfish:/ # cat /dev/smd7 &
sunfish:/ # echo -e 'at+crsm=214,28423,0,0,9,"xxxxxxxxxxxxxxxxxx"\r' > /dev/smd7
sunfish:/ # at+crsm=214,28423,0,0,9,"xxxxxxxxxxxxxxxxxx"
+CRSM: 105,130,""

OK
```

### pysim on phone sim

see [this](./pysim.md)
