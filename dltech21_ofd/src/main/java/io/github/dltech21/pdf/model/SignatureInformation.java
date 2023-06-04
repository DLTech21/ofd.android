package io.github.dltech21.pdf.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by donal on 2018/2/1.
 */

public class SignatureInformation implements Serializable {
    private String fieldName;
    private boolean isSignatureValid;
    private String signer;
    private String signTime;
    private int hasTs;
    private String tsTime;
    private String subject;
    private String issuer;
    private String startTime;
    private String endTime;
    private String serial;
    private String alg;
    private float[] rect;
    private int pageNo;
    private boolean isCheck = false;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public float[] getRect() {
        return rect;
    }

    public void setRect(float[] rect) {
        this.rect = rect;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    @Override
    public String toString() {
        return "SignatureInformation{" +
                "fieldName='" + fieldName + '\'' +
                ", isSignatureValid=" + isSignatureValid +
                ", signer='" + signer + '\'' +
                ", signTime='" + signTime + '\'' +
                ", hasTs=" + hasTs +
                ", tsTime='" + tsTime + '\'' +
                ", subject='" + subject + '\'' +
                ", issuer='" + issuer + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", serial='" + serial + '\'' +
                ", alg='" + alg + '\'' +
                ", rect=" + Arrays.toString(rect) +
                ", pageNo=" + pageNo +
                ", isCheck=" + isCheck +
                '}';
    }

    public String toKeyId() {
        return "signTime='" + signTime + '\'' +
                ", rect=" + Arrays.toString(rect) +
                ", pageNo=" + pageNo;
    }

    public boolean isSignatureValid() {
        return isSignatureValid;
    }

    public void setSignatureValid(boolean signatureValid) {
        isSignatureValid = signatureValid;
    }

    public String getSigner() {
        return signer;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }

    public String getSignTime() {
        return signTime;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }

    public int getHasTs() {
        return hasTs;
    }

    public void setHasTs(int hasTs) {
        this.hasTs = hasTs;
    }

    public String getTsTime() {
        return tsTime;
    }

    public void setTsTime(String tsTime) {
        this.tsTime = tsTime;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getAlg() {
        return alg;
    }

    public void setAlg(String alg) {
        this.alg = alg;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
