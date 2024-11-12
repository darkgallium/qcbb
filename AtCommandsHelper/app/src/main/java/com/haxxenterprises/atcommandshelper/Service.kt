package com.haxxenterprises.atcommandshelper

import android.telephony.TelephonyManager
import android.util.Log
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ServerSocket

fun parseHexBinary(s: String): UByteArray? {
    val len = s.length

    // "111" is not a valid hex encoding.
    require(len % 2 == 0) { "hexBinary needs to be even-length: $s" }
    val out = UByteArray(len / 2)
    var i = 0
    while (i < len) {
        val h = hexToBin(s[i])
        val l = hexToBin(s[i + 1])
        require(!(h == -1 || l == -1)) { "contains illegal character for hexBinary: $s" }
        out[i / 2] = (h * 16 + l).toUByte()
        i += 2
    }
    return out
}

private fun hexToBin(ch: Char): Int {
    if (ch in '0'..'9') {
        return ch.code - '0'.code
    }
    if (ch in 'A'..'F') {
        return ch.code - 'A'.code + 10
    }
    return if (ch in 'a'..'f') {
        ch.code - 'a'.code + 10
    } else -1
}

class Service(tm: TelephonyManager) : Thread() {
    private val telephonyManager = tm
    public override fun run() {
        Log.d("test2", "hasCarrierPrivileges:" + telephonyManager.hasCarrierPrivileges())

        val server = ServerSocket(9999)
        while (true) {
            val conn = server.accept();
            val reader = BufferedReader(InputStreamReader(conn.getInputStream()))
            val writer = BufferedWriter(OutputStreamWriter(conn.getOutputStream()));
            do {
                val line = reader.readLine()
                /*val array = line.split(' ')
                val ins = parseHexBinary(array[0])!![0]
                val cla = parseHexBinary(array[1])!![0]
                val p1 =  parseHexBinary(array[2])!![0]
                val p2 =  parseHexBinary(array[3])!![0]
                val p3 =  parseHexBinary(array[4])!![0]
                val p4 =  parseHexBinary(array[5])*/
                if (line.equals("AT+CSIM=?") || !line.startsWith("AT+CSIM")) {
                    Log.d("unimplemented", line);
                    writer.write("OK");
                    writer.flush();
                } else {
                    Log.d("csim", line);
                    val str = line.split(",")[1]
                    //Log.d("str", str.substring(1, str.length-1));
                    val apdu = str.substring(1, str.length-1);
                    val cla = Integer.parseInt(apdu.substring(0,2), 16);
                    val ins = Integer.parseInt(apdu.substring(2,4), 16);
                    val p1 =  Integer.parseInt(apdu.substring(4,6), 16);
                    val p2 =  Integer.parseInt(apdu.substring(6,8), 16);
                    val p3 =  Integer.parseInt(apdu.substring(8,10), 16);
                    Log.d("str", line);
                    //Log.d("str", String.format("cla=%x,ins=%x,p1=%x,p2=%x,p3=%x", cla, ins, p1, p2, p3));
                    //Log.d("str", apdu.substring(10).lowercase());

                    // TODO: magic

                    //val t = telephonyManager.iccOpenLogicalChannel("A0000000871002FF49FF0589", 0);

                    val res = telephonyManager.iccTransmitApduBasicChannel(cla, ins, p1, p2, p3, apdu.substring(10).lowercase());

                    Log.d("str", res);

                    writer.write(String.format("+CSIM: %d,\"%s\"\r\nOK", res.length, res));
                    writer.flush();
                }

            } while(true);
        }
    }
}