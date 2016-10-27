package com.example.chirs.rxsimpledemo.entity;

import java.util.List;

/**
 * Created by jianjianhong on 2016/9/26.
 */
public class ShowObject {
    private String utdtguid;
    private String utdtversion;
    private String fcreatedate;
    private String utdtprojectversion_fcode;
    private String utdtprojectshow_fcode;
    private String utdtprojectobject_fcode;
    private String utdtobject_fcode;
    private String fcode;
    private String description;
    private String utdtcomponenttype_fcode;
    private String utdtdatasource_fcode;
    private String issavefile;
    private String issavedb;
    private String issavememory;
    private String issavenetwork;
    private String fisneedversion;
    private String datalastmodifydate;
    private String definelastmodifydate;
    private String extendparam;
    private List<ShowObjectField> ltfield;

    public String getUtdtguid() {
        return utdtguid;
    }

    public void setUtdtguid(String utdtguid) {
        this.utdtguid = utdtguid;
    }

    public String getUtdtversion() {
        return utdtversion;
    }

    public void setUtdtversion(String utdtversion) {
        this.utdtversion = utdtversion;
    }

    public String getFcreatedate() {
        return fcreatedate;
    }

    public void setFcreatedate(String fcreatedate) {
        this.fcreatedate = fcreatedate;
    }

    public String getUtdtprojectversion_fcode() {
        return utdtprojectversion_fcode;
    }

    public void setUtdtprojectversion_fcode(String utdtprojectversion_fcode) {
        this.utdtprojectversion_fcode = utdtprojectversion_fcode;
    }

    public String getUtdtprojectshow_fcode() {
        return utdtprojectshow_fcode;
    }

    public void setUtdtprojectshow_fcode(String utdtprojectshow_fcode) {
        this.utdtprojectshow_fcode = utdtprojectshow_fcode;
    }

    public String getUtdtprojectobject_fcode() {
        return utdtprojectobject_fcode;
    }

    public void setUtdtprojectobject_fcode(String utdtprojectobject_fcode) {
        this.utdtprojectobject_fcode = utdtprojectobject_fcode;
    }

    public String getUtdtobject_fcode() {
        return utdtobject_fcode;
    }

    public void setUtdtobject_fcode(String utdtobject_fcode) {
        this.utdtobject_fcode = utdtobject_fcode;
    }

    public String getFcode() {
        return fcode;
    }

    public void setFcode(String fcode) {
        this.fcode = fcode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUtdtcomponenttype_fcode() {
        return utdtcomponenttype_fcode;
    }

    public void setUtdtcomponenttype_fcode(String utdtcomponenttype_fcode) {
        this.utdtcomponenttype_fcode = utdtcomponenttype_fcode;
    }

    public String getUtdtdatasource_fcode() {
        return utdtdatasource_fcode;
    }

    public void setUtdtdatasource_fcode(String utdtdatasource_fcode) {
        this.utdtdatasource_fcode = utdtdatasource_fcode;
    }

    public String getIssavefile() {
        return issavefile;
    }

    public void setIssavefile(String issavefile) {
        this.issavefile = issavefile;
    }

    public String getIssavedb() {
        return issavedb;
    }

    public void setIssavedb(String issavedb) {
        this.issavedb = issavedb;
    }

    public String getIssavememory() {
        return issavememory;
    }

    public void setIssavememory(String issavememory) {
        this.issavememory = issavememory;
    }

    public String getIssavenetwork() {
        return issavenetwork;
    }

    public void setIssavenetwork(String issavenetwork) {
        this.issavenetwork = issavenetwork;
    }

    public String getFisneedversion() {
        return fisneedversion;
    }

    public void setFisneedversion(String fisneedversion) {
        this.fisneedversion = fisneedversion;
    }

    public String getDatalastmodifydate() {
        return datalastmodifydate;
    }

    public void setDatalastmodifydate(String datalastmodifydate) {
        this.datalastmodifydate = datalastmodifydate;
    }

    public String getDefinelastmodifydate() {
        return definelastmodifydate;
    }

    public void setDefinelastmodifydate(String definelastmodifydate) {
        this.definelastmodifydate = definelastmodifydate;
    }

    public String getExtendparam() {
        return extendparam;
    }

    public void setExtendparam(String extendparam) {
        this.extendparam = extendparam;
    }

    public List<ShowObjectField> getLtfield() {
        return ltfield;
    }

    public void setLtfield(List<ShowObjectField> ltfield) {
        this.ltfield = ltfield;
    }

    @Override
    public String toString() {
        return "ShowObject{" +
                "utdtguid='" + utdtguid + '\'' +
                ", utdtversion='" + utdtversion + '\'' +
                ", fcreatedate='" + fcreatedate + '\'' +
                ", utdtprojectversion_fcode='" + utdtprojectversion_fcode + '\'' +
                ", utdtprojectshow_fcode='" + utdtprojectshow_fcode + '\'' +
                ", utdtprojectobject_fcode='" + utdtprojectobject_fcode + '\'' +
                ", utdtobject_fcode='" + utdtobject_fcode + '\'' +
                ", fcode='" + fcode + '\'' +
                ", description='" + description + '\'' +
                ", utdtcomponenttype_fcode='" + utdtcomponenttype_fcode + '\'' +
                ", utdtdatasource_fcode='" + utdtdatasource_fcode + '\'' +
                ", issavefile='" + issavefile + '\'' +
                ", issavedb='" + issavedb + '\'' +
                ", issavememory='" + issavememory + '\'' +
                ", issavenetwork='" + issavenetwork + '\'' +
                ", fisneedversion='" + fisneedversion + '\'' +
                ", datalastmodifydate='" + datalastmodifydate + '\'' +
                ", definelastmodifydate='" + definelastmodifydate + '\'' +
                ", extendparam='" + extendparam + '\'' +
                ", ltfield=" + ltfield +
                '}';
    }
}
