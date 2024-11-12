# pysim wrapper

stub kotlin application (with MODIFY_PHONE_STATE perm) to relay AT commands received over TCP/9999 to TelephonyManager.iccTransmitApduBasicChannel

In order to have MODIFY_PHONE_STATE perm, the app is packaged as a Magisk module that puts the APK (and appropriate permission metadata file) in /system/priv-app. Note that the app is not considered a carrierapp nevertheless privileged TelephonyManager calls are authorized with this perm.

TelephonyManager.iccTransmitApduBasicChannel does not have filtering over AT+CSIM contrary to QC serial AT iface

socat bridge : `socat -x pty,link=/dev/virtualcom0,rawer,b115200,crnl tcp:127.0.0.1:9999,crnl` (chmod 666 /dev/virtualcom0)
adb forward :  adb forward tcp:9999 tcp:9999

pysim must be patched bc mainline pysim does not work well with AT modem transport, this is sufficient to get a pysim shell

```
./pySim-shell.py -a 55555555 --modem-device /dev/virtualcom0
Using modem for Generic SIM Access (3GPP TS 27.007) at port /dev/virtualcom0
Waiting for card...
Warning: Could not detect card type - assuming a generic card type...
Info: Card is of type: UICC
AIDs on card:
 USIM: a0000000xxxxxxxxxxxxxxxx (EF.DIR)
 ISIM: a0000000xxxxxxxxxxxxxxxx (EF.DIR)
 unknown: a000000xxxxxxxxxxxxxxxxxxxxxxxxx (EF.DIR)
Welcome to pySim-shell!
pySIM-shell (00:MF)> select ADF.USIM
```

{
    "file_descriptor": {
        "file_descriptor_byte": {
            "shareable": true,
            "file_type": "df",
            "structure": "no_info_given"
        },
        "record_len": null,
        "num_of_rec": null
    },
    "df_name": "a0000000xxxxxxxxxxxxxxxx",
    "life_cycle_status_integer": "operational_activated",
    "security_attrib_referenced": {
        "ef_arr_file_id": "2f06",
        "ef_arr_record_nr": 1
    },
    "pin_status_template_do": {
        "ps_do": "a0",
        "key_reference": 10
    }
}
pySIM-shell (00:MF/ADF.USIM)> select
.                   6f4b                6fc9                ADF.USIM            EF.EXT3             EF.NETPAR
..                  6f4c                6fca                DF.5G_ProSe         EF.EXT4             EF.NIA
3f00                6f4d                6fcb                DF.5GS              EF.EXT5             EF.OCI
5f3a                6f4e                6fcc                DF.GSM-ACCESS       EF.EXT6             EF.OCT
5f3b                6f4f                6fcd                DF.HNB              EF.EXT7             EF.OPL
5f40                6f50                6fce                DF.PHONEBOOK        EF.EXT8             EF.OPLMNwAcT
5f50                6f55                6fcf                DF.ProSe            EF.FDN              EF.PLMNwAcT
5f90                6f56                6fd0                DF.SAIP             EF.FDNURI           EF.PNN
5fc0                6f57                6fd1                DF.SNPN             EF.FPLMN            EF.PNNI
5fd0                6f58                6fd2                DF.WLAN             EF.FromPreferred    EF.PSLOCI
5fe0                6f5b                6fd3                EF.AAeM             EF.GBABP            EF.PUCT
5ff0                6f5c                6fd4                EF.ACC              EF.GBANL            EF.PWS
6f01                6f60                6fd5                EF.ACL              EF.GID1             EF.RPLMNAcTD
6f05                6f61                6fd6                EF.ACM              EF.GID2             EF.SDN
6f06                6f62                6fd7                EF.ACMmax           EF.HPLMNwAcT        EF.SDNURI
6f07                6f65                6fd8                EF.AD               EF.HPPLMN           EF.SMS
6f08                6f73                6fd9                EF.ARR              EF.ICI              EF.SMSP
6f09                6f78                6fda                EF.BDN              EF.ICT              EF.SMSR
6f2c                6f7b                6fdb                EF.BDNURI           EF.IMSI             EF.SMSS
6f31                6f7e                6fdd                EF.CBMI             EF.IPS              EF.SPDI
6f32                6f80                6fde                EF.CBMID            EF.Keys             EF.SPN
6f37                6f81                6fdf                EF.CBMIR            EF.KeysPS           EF.SPNI
6f38                6f82                6fe2                EF.CCP2             EF.LI               EF.START-HFN
6f39                6f83                6fe3                EF.CFIS             EF.LOCI             EF.THRESHOLD
6f3b                6fad                6fe4                EF.CMI              EF.MBDN             EF.UFC
6f3c                6fb1                6fe6                EF.CNL              EF.MBI              EF.UST
6f3e                6fb2                6fe8                EF.DCK              EF.MMSICP           EF.VBCSCA
6f3f                6fb3                6fec                EF.eAKA             EF.MMSN             EF.VBS
6f40                6fb4                6fed                EF.ECC              EF.MMSUCP           EF.VBSS
6f41                6fb5                6fee                EF.eDPDGId          EF.MMSUP            EF.VGCS
6f42                6fb6                6fef                EF.EHPLMN           EF.MSISDN           EF.VGCSCA
6f43                6fb7                6ff1                EF.EHPLMNPI         EF.MSK              EF.VGCSS
6f45                6fc4                6ff3                EF.eMLPP            EF.MUK              MF
6f46                6fc5                6ff7                EF.EPSLOCI          EF.MWIS
6f47                6fc6                xxxxxxxxxxxxxx      EF.EPSNSC           EF.NAFKCA
6f48                6fc7                xxxxxxxxxxxxxx      EF.EST              EF.NASCONFIG
6f49                6fc8                ADF.ISIM            EF.EXT2             EF.NCP-IP
pySIM-shell (00:MF/ADF.USIM)> select EF.IMSI
{
    "file_descriptor": {
        "file_descriptor_byte": {
            "shareable": true,
            "file_type": "working_ef",
            "structure": "transparent"
        },
        "record_len": null,
        "num_of_rec": null
    },
    "file_identifier": "6f07",
    "life_cycle_status_integer": "operational_activated",
    "security_attrib_referenced": {
        "ef_arr_file_id": "6f06",
        "ef_arr_record_nr": 10
    },
    "file_size": 9,
    "short_file_identifier": 7
}
```
