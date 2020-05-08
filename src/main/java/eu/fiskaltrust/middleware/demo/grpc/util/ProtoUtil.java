package eu.fiskaltrust.middleware.demo.grpc.util;

import bcl.Bcl;

import java.util.Date;
import java.util.regex.Pattern;

public class ProtoUtil {

    public static Bcl.Decimal parseDecimal(Object obj) {
        if (obj == null)
            return null;

        String str = obj.toString().replace("-", "");

        // TODO: Replace this workaround with a proper serialization and also include the HI bits, instead of just cutting after 15 characters
        str = str.length() > 15 ? str.substring(0, 15) : str;
        String[] split = str.split(Pattern.quote("."));
        int precision = split.length == 2 ? split[1].length() : 0;
        int shift = str.startsWith("-") ? 0 : 1;

        return Bcl.Decimal.newBuilder()
                .setLo(Long.parseLong(str.replace(".", "")))
                .setSignScale(precision << shift)
                .build();
    }

    public static Bcl.DateTime parseDatetime(String value) throws java.text.ParseException {
        if (value == null)
            return null;

        Date date = DateUtil.parse(value);
        return Bcl.DateTime.newBuilder()
                .setKindValue(1)   // UTC
                .setScaleValue(4)  // Milliseconds
                .setValue(date.getTime())
                .build();
    }
}
