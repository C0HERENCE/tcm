package cn.ccwisp.tcm.generated.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * null
 * @TableName herbsinfo
 */
@TableName(value ="herbsinfo")
@Data
public class HerbsInfo implements Serializable {
    /**
     * 
     */
    @TableId
    private Integer id;

    /**
     * 
     */
    private String chinesename;

    /**
     * 
     */
    private String chinesepinyin;

    /**
     * 
     */
    private String latinname;

    /**
     * 
     */
    private String intro;

    /**
     * 
     */
    private String thumbnail;

    /**
     * 
     */
    private String picturepath;

    /**
     * 
     */
    private String cmedicaltype;

    /**
     * 
     */
    private String origin;

    /**
     * 
     */
    private String toxicity;

    /**
     * 
     */
    private String remark;

    /**
     * 
     */
    private String healthtype;

    /**
     * 
     */
    private String alisaname;

    /**
     * 
     */
    private String ctime;

    /**
     * 
     */
    private String func;

    /**
     * 
     */
    private String mainattend;

    /**
     * 
     */
    private String price;

    /**
     * 
     */
    private String drugsite;

    /**
     * 
     */
    private String dosage;

    /**
     * 
     */
    private String configuration;

    /**
     * 
     */
    private String adversereactions;

    /**
     * 
     */
    private String taboo;

    /**
     * 
     */
    private String harvestprocess;

    /**
     * 
     */
    private String processing;

    /**
     * 
     */
    private String storagemethod;

    /**
     * 
     */
    private String medicinalproperty;

    /**
     * 
     */
    private String decoctionpiecescharacter;

    /**
     * 
     */
    private String chemicalcomposition;

    /**
     * 
     */
    private String identify;

    /**
     * 
     */
    private String pharmacycheck;

    /**
     * 
     */
    private String contentpic;

    /**
     * 
     */
    private String fingerprint;

    /**
     * 
     */
    private String pharmacology;

    /**
     * 
     */
    private String toxicologicaleffects;

    /**
     * 
     */
    private String plantgenus;

    /**
     * 
     */
    private String plantfrature;

    /**
     * 
     */
    private String plantcroparea;

    /**
     * 
     */
    private String plantrealestate;

    /**
     * 
     */
    private String plantgrowthenvironment;

    /**
     * 
     */
    private String plantgrowthhabits;

    /**
     * 
     */
    private String plantsiremethods;

    /**
     * 
     */
    private String planttraintechniques;

    /**
     * 
     */
    private String plantpestcontrol;

    /**
     * 
     */
    private String plantmajorvariant;

    /**
     * 
     */
    private String animalgenus;

    /**
     * 
     */
    private String animalfrature;

    /**
     * 
     */
    private String animalcroparea;

    /**
     * 
     */
    private String animalrealestate;

    /**
     * 
     */
    private String animalgrowthenvironment;

    /**
     * 
     */
    private String animalartificialmanufacture;

    /**
     * 
     */
    private String mineralgenus;

    /**
     * 
     */
    private String mineralfrature;

    /**
     * 
     */
    private String resourcedistribution;

    /**
     * 
     */
    private String mineralgrowthenvironment;

    /**
     * 
     */
    private String mineralproperties;

    /**
     * 
     */
    private String clinicalapplication;

    /**
     * 
     */
    private String medicformat;

    /**
     * 
     */
    private String comcounterfeit;

    /**
     * 
     */
    private String modernresearch;

    /**
     * 
     */
    private String notice;

    /**
     * 
     */
    private String relateddiscussion;

    /**
     * 
     */
    private String messagefrom;

    /**
     * 
     */
    private String notes;

    /**
     * 
     */
    private String statement;

    /**
     * 
     */
    private String editdoctor;

    /**
     * 
     */
    private String auditdoctor;

    /**
     * 
     */
    private String herbalpiecesname;

    /**
     * 
     */
    private String herbalpiecesspecification;

    /**
     * 
     */
    private String kingdom;

    /**
     * 
     */
    private String phylum;

    /**
     * 
     */
    private String classify;

    /**
     * 
     */
    private String catalogue;

    /**
     * 
     */
    private String family;

    /**
     * 
     */
    private String genus;

    /**
     * 
     */
    private String species;

    /**
     * 
     */
    private String identifymedication;

    /**
     * 
     */
    private String relateddrug;

    /**
     * 
     */
    private String relatedprescription;

    /**
     * 
     */
    private String dietguidelines;

    /**
     * 
     */
    private String msgtype;

    /**
     * 
     */
    private String msgtypecontent;

    /**
     * 
     */
    private String comcounterfeitimg;

    /**
     * 
     */
    private String medicinalpropertyimg;

    /**
     * 
     */
    private String realestate;

    /**
     * 
     */
    private String mineralrealestate;

    /**
     * 
     */
    private String medicinalslicesimg;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}