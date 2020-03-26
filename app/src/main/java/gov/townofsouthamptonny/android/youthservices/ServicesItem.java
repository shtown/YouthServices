package gov.townofsouthamptonny.android.youthservices;

import java.util.UUID;

/**
 * Created by JDaly on 2/24/2016.
 */
public class ServicesItem {
    private UUID mUUID;
    private String Phone1Ext;
    private String ID;
    private String Email;
    private String Zip;
    private String AddressLine3;
    private String IP_Address;
    private String Mapped;
    private String Fee;
    private String loc_ID;
    private String xCoord;
    private String Phone2;
    private String Lon;
    private String WebLink;
    private String Civic;
    private String Fax;
    private String AddressLine2;
    private String category;
    private String SubmissionDate;
    private String yCoord;
    private String AddressLine1;
    private String Lat;
    private String Desc;
    private String Address;
    private String F_Name;
    private String Contact;
    private String Phone2xt;
    private String Title;
    private String Phone1;
    private String Township;
    private double DistFromCenter;



    public ServicesItem() {
        this(UUID.randomUUID());
        mUUID= UUID.randomUUID();
    }

    public ServicesItem(UUID id)  {
        mUUID = id;
    }

    public UUID getUUID() {
        return mUUID;
    }

    public String getPhone1Ext() {
        return Phone1Ext;
    }

    public void setPhone1Ext(String phone1Ext) {
        Phone1Ext = phone1Ext;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getZip() {
        return Zip;
    }

    public void setZip(String zip) {
        Zip = zip;
    }

    public String getAddressLine3() {
        return AddressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        AddressLine3 = addressLine3;
    }

    public String getIP_Address() {
        return IP_Address;
    }

    public void setIP_Address(String IP_Address) {
        this.IP_Address = IP_Address;
    }

    public String getMapped() {
        return Mapped;
    }

    public void setMapped(String mapped) {
        Mapped = mapped;
    }

    public String getFee() {
        return Fee;
    }

    public void setFee(String fee) {
        Fee = fee;
    }

    public String getLoc_ID() {
        return loc_ID;
    }

    public void setLoc_ID(String loc_ID) {
        this.loc_ID = loc_ID;
    }

    public String getxCoord() {
        return xCoord;
    }

    public void setxCoord(String xCoord) {
        this.xCoord = xCoord;
    }

    public String getPhone2() {
        return Phone2;
    }

    public void setPhone2(String phone2) {
        Phone2 = phone2;
    }

    public String getLon() {
        return Lon;
    }

    public void setLon(String lon) {
        Lon = lon;
    }

    public String getWebLink() {
        return WebLink;
    }

    public void setWebLink(String webLink) {
        WebLink = webLink;
    }

    public String getCivic() {
        return Civic;
    }

    public void setCivic(String civic) {
        Civic = civic;
    }

    public String getFax() {
        return Fax;
    }

    public void setFax(String fax) {
        Fax = fax;
    }

    public String getAddressLine2() {
        return AddressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        AddressLine2 = addressLine2;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubmissionDate() {
        return SubmissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        SubmissionDate = submissionDate;
    }

    public String getyCoord() {
        return yCoord;
    }

    public void setyCoord(String yCoord) {
        this.yCoord = yCoord;
    }

    public String getAddressLine1() {
        return AddressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        AddressLine1 = addressLine1;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getF_Name() {
        return F_Name;
    }

    public void setF_Name(String f_Name) {
        F_Name = f_Name;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getPhone2xt() {
        return Phone2xt;
    }

    public void setPhone2xt(String phone2xt) {
        Phone2xt = phone2xt;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getPhone1() {
        return Phone1;
    }

    public void setPhone1(String phone1) {
        Phone1 = phone1;
    }

    public double getDistFromCenter() {
        return DistFromCenter;
    }

    public void setDistFromCenter(double distFromCenter) {
        DistFromCenter = distFromCenter;
    }


    public String getTownship() {
        return Township;
    }

    public void setTownship(String township) {
        Township = township;
    }


}
