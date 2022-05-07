package Treatment;

import java.util.ArrayList;
import java.util.Hashtable;

import BaseClass.BaseRecord;
import Person.PersonUtils;
import PrefixState.Prefix;
import Utility.DataUtils;


/**
 * Copyright (C) 2022-2022, HDM-Dev Team
 * All Rights Reserved

 * This file is part of HDM-Dev Team's project. The contents are
 * fully covered, controlled, and acknowledged by the terms of the
 * BSD-3 license, which is included in the file LICENSE.md, found
 * at the root of the project's source code/tree repository.
**/

/**
 * This class is a small wrapper which controlled the basic behaviour of the "Treatment" class.
 * So what fields should we implemented to contain in this class? 
 * 
 * => In general, what we should implement is that 
 * 1) The ID and some syncronized information of the "Patient"
 * 2) The collection of "Treatment" inside this "MedicalRecord"
 * 3) The medico(s) who is/are responsible for this "MedicalRecord". Note that this information
 *    is sometimes useless in some scenarios, for example, during an traffic-rescuing emergency.
 *    Thus these information is not needed in some scenarios.
 * 
 * 
 * @author Ichiru Take
 * @version 0.0.1
 * 
 * References:
 * 1) 
**/


public class MedicalRecord extends BaseRecord {
    // ---------------------------------------------------------------------------------------------------------------------
    // Synchronized with Treatment.StandardizeIndex()
    private static final int NUMBER_OF_MAX_TREATMENTS = Treatment.GetMaxTreatmentInMedicalRecords();       
    private final ArrayList<Treatment> LocalPool;

    // ----------------------------------------------------------                         
    private String MedicalRecordID;
    private String RDoc_MedicoID, RNurse_MedicoID;             // This is the ID of the doctor and nurse who created the record    
    protected Prefix prefix;

    public MedicalRecord(String Patient_ID, String Pt_FirstName, String Pt_LastName, String Pt_Age, String Pt_Gender, 
                         boolean writable, String MedicalRecord_ID) {
        super(Patient_ID, Pt_FirstName, Pt_LastName, Pt_Age, Pt_Gender, writable);
        
        DataUtils.CheckArgumentCondition(MedicalRecord_ID != null, "The MedicalRecord_ID cannot be null.");
        DataUtils.CheckArgumentCondition(MedicalRecord_ID.length() > 0, "The MedicalRecord_ID cannot be empty.");
        this.MedicalRecordID = MedicalRecord_ID;
        this.prefix = MedicalRecord.GetPrefix();
        this.LocalPool = new ArrayList<Treatment>(MedicalRecord.NUMBER_OF_MAX_TREATMENTS);
        
        this.RDoc_MedicoID = "";                           // Default is empty field
        this.RNurse_MedicoID = "";                          // Default is empty field
    }

    public MedicalRecord(String Patient_ID, String Pt_FirstName, String Pt_LastName, String Pt_Age, String Pt_Gender, 
                         String MedicalRecord_ID) {
        this(Patient_ID, Pt_FirstName, Pt_LastName, Pt_Age, Pt_Gender, true, 
             MedicalRecord_ID);
    }

    public MedicalRecord(String Patient_ID, String Pt_FirstName, String Pt_LastName, String Pt_Age, String Pt_Gender, 
                         boolean writable, int MedicalRecord_ID) {
        this(Patient_ID, Pt_FirstName, Pt_LastName, Pt_Age, Pt_Gender, true, 
             String.valueOf(MedicalRecord_ID));        
    }

    public MedicalRecord(String Patient_ID, String Pt_FirstName, String Pt_LastName, String Pt_Age, String Pt_Gender) {
        this(Patient_ID, Pt_FirstName, Pt_LastName, Pt_Age, Pt_Gender, true, 
             -1);
    }

    // --------------------------------------------------------------------------------------------------------------------
    // Getter & Setter
    public static int GetMaxNumberOfTreatments() { return MedicalRecord.NUMBER_OF_MAX_TREATMENTS; }

    public ArrayList<Treatment> GetLocalPool() { return this.LocalPool; }
    public String GetMedicalRecordID() {  return this.MedicalRecordID; }

    public void SetMedicalRecordID(String MedicalRecordID) throws Exception { 
        DataUtils.CheckArgumentCondition(MedicalRecordID != null, "ID cannot be null.");
        DataUtils.CheckArgumentCondition(MedicalRecordID.length() > 0, "ID cannot be empty.");
        
        if (this.GetMedicalRecordID().equals("-1")) { this.MedicalRecordID = MedicalRecordID; }
        throw new Exception("The ID cannot be changed.");
    }

    public static Prefix GetPrefix() { return Prefix.MedicalRecord; }
    public Prefix GetThisPrefix() { return this.prefix; }

    public static String GetPrefixCode() { return MedicalRecord.GetPrefix().GetPrefixCode(); }
    public String GetThisPrefixCode() { return this.GetThisPrefix().GetPrefixCode(); }

    // ----------------------------------------------------------
    public String GetRDoc_MedicoID() { return this.RDoc_MedicoID; }
    public void SetRDoc_MedicoID(String RDoc_MedicoID) throws Exception {
        DataUtils.CheckArgumentCondition(RDoc_MedicoID != null, "ID cannot be null.");
        this.RDoc_MedicoID = RDoc_MedicoID;
    }

    public String GetRNurse_MedicoID() { return this.RNurse_MedicoID; }
    public void SetRNurse_MedicoID(String RNurse_MedicoID) throws Exception {
        DataUtils.CheckArgumentCondition(RNurse_MedicoID != null, "ID cannot be null.");
        this.RNurse_MedicoID = RNurse_MedicoID;
    }

    // --------------------------------------------------------------------------------------------------------------------
    // Treatment-related methods
    private void AttemptToSetTreatmentIndex(Treatment treatment) {
        if (treatment.GetTreatmentIndex() == -1) { treatment.SetTreatmentIndex(this.GetLocalPool().size()); }
    }

    public void AddTreatment(Treatment treatment) throws Exception {
        DataUtils.CheckArgumentCondition(this.IsWritable(), "This record is not writable.");
        DataUtils.CheckArgumentCondition(treatment != null, "The treatment cannot be null.");
        DataUtils.CheckCondition(this.GetLocalPool().size() < MedicalRecord.GetMaxNumberOfTreatments(), 
                                 "The number of treatments stored is too large.");
        
        DataUtils.CheckCondition(treatment.GetMedicalRecordID() == this.GetMedicalRecordID(), 
                                 "This treatment does not belong to this medical record.");
        BaseRecord.ValidateTwoNeighborRecords(this, treatment, true);
        this.AttemptToSetTreatmentIndex(treatment);
        this.LocalPool.add(treatment);
    }

    public Treatment CreateNewTreatment(String code) throws Exception {
        // Note that this method does not add the new treatment to the local pool.
        // It only creates a new treatment object.
        String Pt_ID = this.GetPtID();
        String Pt_FirstName = this.GetPtFirstName();
        String Pt_LastName = this.GetPtLastName();
        String Pt_Age = this.GetPtAge();
        String Pt_Gender = this.GetPtGender();
        String MedicalRecord_ID = this.GetMedicalRecordID();

        Treatment newTreatment = new Treatment(Pt_ID, MedicalRecord_ID, Pt_FirstName, Pt_LastName, Pt_Age, Pt_Gender, 
                                               -1, code, true) ;
        return newTreatment;  
    }

    public Treatment AddNewTreatment(String code) throws Exception {
        DataUtils.CheckArgumentCondition(this.IsWritable(), "This record is not writable.");
        DataUtils.CheckCondition(this.GetLocalPool().size() < MedicalRecord.GetMaxNumberOfTreatments(), 
                                 "The number of treatments stored is too large.");
        
        Treatment treatment = this.CreateNewTreatment(code);
        this.AttemptToSetTreatmentIndex(treatment);
        this.LocalPool.add(treatment);
        return treatment;
    }

    public Treatment GetTreatment(int index) throws Exception {
        DataUtils.CheckArgumentCondition(index >= 0, "The index cannot be negative.");
        DataUtils.CheckArgumentCondition(index < this.GetLocalPool().size(), "The index is out of range.");
        return this.GetLocalPool().get(index);
    }

    // --------------------------------------------------------------------------------------------------------------------
    // TODO Serialization & Deserialization
    public Hashtable<String, Object> Serialize() {
		Hashtable<String, Object> TreatmentInformation = super.Serialize();
		TreatmentInformation.put("MedicalRecordID", this.GetMedicalRecordID());
		TreatmentInformation.put("TreatmentIndex", (Object) this.GetTreatmentIndex());
		TreatmentInformation.put("ClassificationCode", this.GetClassificationCode());
		
		String directory, folder;
		try { 
			folder = PersonUtils.GetPatientRecordDirectory(this.GetPtFirstName(), false); 
			TreatmentInformation.put("folder", folder); 		// Saved here to prevent failed compilation
		} catch (Exception e) { // This is never called as standardization is done in the Patient class.
			e.printStackTrace();
		}
		
		// Note that at here the directory is: "database/PatientRecord/[FirstName-Tree]/".
		// To reach the true directory, we need to add the "Patient.ID" and "MedicalRecord.ID into it"
		// The result is: "database/PatientRecord/[FirstName-Tree]/[Patient.ID]/[MedicalRecord.ID]/".
		folder = TreatmentInformation.get("folder") + this.GetPtID() + "/" + this.GetMedicalRecordID() + "/";
		TreatmentInformation.put("folder", folder);

		// After that, we needed to deepen down to the "TreatmentIndex"
		// The result is: "database/PatientRecord/[FirstName-Tree]/[Patient.ID]/[MedicalRecord.ID]/[TreatmentIndex]/".
		String Subfolder = folder + this.GetTreatmentIndexAsString() + "/";
		TreatmentInformation.put("Subfolder", Subfolder); 	// Saved here as cache	

		try {
			directory = Subfolder + "MedicoInfo.json";
			JsonUtils.SaveHashTableIntoJsonFile(directory, this.GetMedicoInfo(), null);
			TreatmentInformation.put("MedicoInfo", directory);

			ArrayList<Object> CastedSupplementary = DataUtils.CastToObjectArrayFromStringArray(this.GetSupplementary());
			directory = Subfolder + "Supplementary.json";
			JsonUtils.SaveArrayListIntoJsonFile(directory, CastedSupplementary, null);
			TreatmentInformation.put("Supplementary", directory);

			directory = Subfolder + "Resources.json";
			JsonUtils.SaveHashTableIntoJsonFile(directory, this.GetResources(), null);
			TreatmentInformation.put("Resources", directory);

			directory = Subfolder + "Descriptions.json";
			JsonUtils.SaveHashTableIntoJsonFile(directory, this.GetDescriptions(), null);
			TreatmentInformation.put("Descriptions", directory);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return TreatmentInformation;
	}

	public static Treatment Deserialize(Hashtable<String, Object> data) {
		String Pt_ID = (String) data.get("Pt_ID");
        String Pt_FirstName = (String) data.get("Pt_FirstName");
        String Pt_LastName = (String) data.get("Pt_LastName");
        String Pt_Age = (String) data.get("Pt_Age");
        String Pt_Gender = (String) data.get("Pt_Gender");

		String MedicalRecordID = (String) data.get("MedicalRecordID");
		int TreatmentIndex = Integer.parseInt((String) data.get("TreatmentIndex"));
		String ClassificationCode = (String) data.get("ClassificationCode");

        Treatment record = new Treatment(Pt_ID, MedicalRecordID, Pt_FirstName, Pt_LastName, Pt_Age, 
										 Pt_Gender, TreatmentIndex, ClassificationCode, true);
        record.SetDate((String) data.get("date"));
        record.SetTime((String) data.get("time"));

		// Deserialize Medico, Supplementary, Resources, and Descriptions. These are stored in JSON files.
		// So we need to call them
		String MedicoInfo_File = (String) data.get("MedicoInfo");
		String Supplementary_File = (String) data.get("Supplementary");
		String Resources_File = (String) data.get("Resources");
		String Descriptions_File = (String) data.get("Descriptions");

		try {
			record.GetMedicoInfo().putAll(JsonUtils.LoadJsonFileToHashtable(MedicoInfo_File, null));

			ArrayList<Object> Supplementary = JsonUtils.LoadJsonFileToArrayList(Supplementary_File, null);
			record.GetSupplementary().addAll(DataUtils.CastToStringArrayFromObjectArray(Supplementary));

			record.GetResources().putAll(JsonUtils.LoadJsonFileToHashtable(Resources_File, null));
			record.GetDescriptions().putAll(JsonUtils.LoadJsonFileToHashtable(Descriptions_File, null));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(boolean) data.get("writable")) { record.CloseRecord(); }
		return record;
	}

}
