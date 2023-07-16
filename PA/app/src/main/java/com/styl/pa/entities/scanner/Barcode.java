package com.styl.pa.entities.scanner;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.styl.pa.utils.LogManager;

import java.util.Locale;

import kotlin.text.Charsets;

public class Barcode implements Parcelable {
    public static final String NRIC_TYPE = "NRIC";
    public static final String FIC_TYPE = "FIN";
    public static final String PASSPORT_TYPE = "Passport";

    byte[] barcodeData;
    int barcodeType;
    int fromScannerID;

    public Barcode(byte[] barcodeData, int barcodeType, int fromScannerID) {
        this.barcodeData = barcodeData;
        this.barcodeType = barcodeType;
        this.fromScannerID = fromScannerID;
    }

    public Barcode(byte[] barcodeData) {
        this.barcodeData = barcodeData;
    }

    public Barcode(Parcel in) {
        this.barcodeData = in.readString().getBytes();
        this.barcodeType = in.readInt();
        this.fromScannerID = in.readInt();
    }

    public byte[] getBarcodeData() {
        return barcodeData;
    }

    public void setBarcodeData(byte[] barcodeData) {
        this.barcodeData = barcodeData;
    }

    public int getFromScannerID() {
        return fromScannerID;
    }

    public void setFromScannerID(int fromScannerID) {
        this.fromScannerID = fromScannerID;
    }

    public int getBarcodeType() {
        return barcodeType;
    }

    public void setBarcodeType(int barcodeType) {
        this.barcodeType = barcodeType;
    }

    public String getBarcodeDataFormat() {
        return TextUtils.isEmpty(new String(barcodeData, Charsets.UTF_8)) ? "" : new String(barcodeData, Charsets.UTF_8);
    }

    private boolean isNRICFIN() {
        String barcodeValue = TextUtils.isEmpty(new String(barcodeData, Charsets.UTF_8)) ? "" : new String(barcodeData, Charsets.UTF_8);
        if (barcodeValue.length() != 9) {
            return false;
        }
        barcodeValue = barcodeValue.toUpperCase(Locale.ENGLISH);

        String[] st = new String[]{"J", "Z", "I", "H", "G", "F", "E", "D", "C", "B", "A"};
        String[] fg = new String[]{"X", "W", "U", "T", "R", "Q", "P", "N", "M", "L", "K"};
        int[] weightAge = new int[]{2, 7, 6, 5, 4, 3, 2};

        int[] digits = new int[7];
        for (int i = 0; i < 7; i++) {
            try {
                digits[i] = Integer.parseInt(String.valueOf(barcodeValue.charAt(i + 1)), 10) * weightAge[i];
            } catch (NumberFormatException e) {
                LogManager.Companion.i("Error when formatting number from NRIC/FIN barcode");
                return false;
            }
        }

        int total = 0;
        for (int digit : digits) {
            total += digit;
        }

        if (barcodeValue.startsWith("T") || barcodeValue.startsWith("G")) {
            total += 4;
        }

        String lastCharacter = "";
        if (barcodeValue.startsWith("S") || barcodeValue.startsWith("T")) {
            lastCharacter = st[total % 11];
        } else if (barcodeValue.startsWith("F") || barcodeValue.startsWith("G")) {
            lastCharacter = fg[total % 11];
        }

        return !TextUtils.isEmpty(lastCharacter) && barcodeValue.endsWith(lastCharacter);
    }

    public String getTypeBarcode() {
        String barcodeValue = TextUtils.isEmpty(new String(barcodeData, Charsets.UTF_8)) ? "" : new String(barcodeData, Charsets.UTF_8);
        if (isNRICFIN()) {
            if (barcodeValue.startsWith("S") || barcodeValue.startsWith("T")) {
                return NRIC_TYPE;
            } else if (barcodeValue.startsWith("F") || barcodeValue.startsWith("G")) {
                return FIC_TYPE;
            }

        }

        return PASSPORT_TYPE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(new String(barcodeData, Charsets.UTF_8));
        parcel.writeInt(barcodeType);
        parcel.writeInt(fromScannerID);
    }

    public static final Creator<Barcode> CREATOR = new Creator<Barcode>() {

        @Override
        public Barcode createFromParcel(Parcel source) {
            return new Barcode(source);
        }

        @Override
        public Barcode[] newArray(int size) {
            return new Barcode[size];
        }
    };
}
