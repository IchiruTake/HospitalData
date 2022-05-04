package BaseClass;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

import BaseClass.AbstractObject;
import Object.Tool;
import Utility.DataUtils;
import Utility.Utils;

/**
 * Copyright (C) 2022-2022, HDM-Dev Team
 * All Rights Reserved
 *
 * This file is part of HDM-Dev Team's project. The contents are
 * fully covered, controlled, and acknowledged by the terms of the
 * BSD-3 license, which is included in the file LICENSE.md, found
 * at the root of the project's source code/tree repository.
**/

/**
 * This class is to build a simple representation of the "Object Manager" which is a parent class
 * of "LToolPool", "LResourcesPool", "GToolPool", "GResourcesPool". The information storage is to 
 * map the ID of the object to {ID, name, amount}.
 * 
 * Designing this class is a little bit tricky and not. The hidden reason is that its structure 
 * is similar to the "PatientRoom" but the limitation from the amount is unknown, which implied 
 * that in theory, the number of "Object" and "Resource" is unlimited. Thus, one of the greatest 
 * option is to set a constraint to prevent memory-overflow and serialize the data.

 *  
 * @author Ichiru Take
 * @version 0.0.1
 * 
 * References:
 * 1) https://stackoverflow.com/questions/30207940/what-happens-to-protected-method-of-a-super-class-in-base-class-in-java
 * 2) https://stackoverflow.com/questions/47800155/is-protected-method-in-super-class-visible-in-sub-class-in-a-different-package
**/

public class BaseObjectPool extends AbstractObject {
    // ---------------------------------------------------------------------------------------------------------------------
    // Plus one here is to store the ID of this class.
    private final static int MAX_CAPACTIY = 10000;
    private final static int SERIALIZATION_CAPACTITY = MAX_CAPACTIY + 1;
    private final static float SERIALIZATION_LOAD_FACTOR = 0.75f;
    private int NumberOfInformation = 3;
    private Hashtable<String, Object> LocalPool;

    public BaseObjectPool(String ID) throws Exception {
        super(ID);
        int capacity = BaseObjectPool.GetSerializationCapacity();
        float loadFactor = BaseObjectPool.GetSerializationLoadFactor();
        this.LocalPool = new Hashtable<String, Object>(capacity, loadFactor);
    }

    public BaseObjectPool(BaseObjectPool obj) throws Exception {
        super(obj.GetID());
        this.LocalPool = obj.LocalPool;
    }

    // ---------------------------------------------------------------------------------------------------------------------
    // Find object in pool
    public static String[] GetObjectInformation(String ID, String name, int number) {
        BaseObjectPool.ValidateInput(ID, name, number);
        String[] ObjectInfo = {ID, name, String.valueOf(number)};
        return ObjectInfo;
    }

    public static String[] GetObjectInformation(AbstractObject object, int number) {
        Utils.CheckArgumentCondition(object != null, "Object cannot be null.");
        return BaseObjectPool.GetObjectInformation(object.GetID(), object.GetName(), number);
    }

    public static Object[] GetObjectInformationAsObjectList(AbstractObject object, int number) {
        Utils.CheckArgumentCondition(object != null, "Object cannot be null.");
        return (Object[]) BaseObjectPool.GetObjectInformation(object, number);
    }

    public static Object GetObjectInformationAsObject(AbstractObject object, int number) {
        Utils.CheckArgumentCondition(object != null, "Object cannot be null.");
        return (Object) BaseObjectPool.GetObjectInformation(object.GetID(), object.GetName(), number);
    }

    public String[] GetObject(String ID) {
        Utils.CheckArgumentCondition(ID != null, "Object's ID cannot be null.");
        return (String[]) this.GetLocalPool().get(ID); 
    }

    public String[] GetObject(AbstractObject object) {
        Utils.CheckArgumentCondition(object != null, "Object cannot be null.");
        return this.GetObject(object.GetID());
    }

    public String[] GetFirstObjectByName(String name) {
        Utils.CheckArgumentCondition(name != null, "Object's Name cannot be null.");
        for (Object Tool : this.GetLocalPool().values()) {
            String[] ToolInformation = (String[]) Tool;
            if (ToolInformation[1].equals(name) || ToolInformation[1].contains(name)) {
                return ToolInformation;
            }
        }
        return null;
    }

    public boolean IsObjectAvailable(AbstractObject object) { return this.GetObject(object.GetID()) != null; }

    public boolean IsObjectAvailable(String ID) { return this.GetObject(ID) != null; }

    // ---------------------------------------------------------------------------------------------------------------------
    // Add-er & Remove-r functions
    public boolean UpdateObject(String ID, int amount) throws Exception {
        BaseObjectPool.ValidateInput(ID, "", amount, false);
        if (!this.IsObjectAvailable(ID)) {
            throw new Exception("Object is not available."); 
        }

        String[] PoolObjectInfo = this.GetObject(ID);
        String PoolAmount = PoolObjectInfo[2];
        int NewAmount = Integer.parseInt(PoolAmount) + amount;
        if (NewAmount == 0) { return this.RemoveObject(ID); }
        
        PoolObjectInfo[2] = String.valueOf(NewAmount);
        this.GetLocalPool().put(ID, PoolObjectInfo);
        return false;
    }

    public boolean UpdateObject(AbstractObject ID, int amount) throws Exception {
        Utils.CheckArgumentCondition(ID != null, "Object cannot be null.");
        return this.UpdateObject(ID.GetID(), amount);
    }

    public boolean AddNewObject(String ID, String name, int amount) throws Exception {
        BaseObjectPool.ValidateInput(ID, name, amount);
        if (this.GetCurrentCapacity() > BaseObjectPool.GetMaxCapacity()) {
            throw new Exception("The pool inside is full.");
        }
        String[] ObjectInfo = BaseObjectPool.GetObjectInformation(ID, name, amount);
        return this.GetLocalPool().put(ID, ObjectInfo) == null;
    }

    public boolean AddNewObject(AbstractObject object, int amount) throws Exception {
        Utils.CheckArgumentCondition(object != null, "Object cannot be null.");
        return this.AddNewObject(object.GetID(), object.GetName(), amount);
    }

    public boolean RemoveObject(String ID) {
        Utils.CheckArgumentCondition(ID != null, "Object's ID cannot be null.");
        return this.GetLocalPool().remove(ID) != null;
    }

    public boolean RemoveObject(AbstractObject object) {
        Utils.CheckArgumentCondition(object != null, "Object cannot be null.");
        return this.RemoveObject(object.GetID());
    }

    // ---------------------------------------------------------------------------------------------------------------------
    // Getter Function
    public Hashtable<String, Object> GetLocalPool() { return this.LocalPool; }   
    private void SetLocalPool(Hashtable<String, Object> LocalPool) { this.LocalPool = LocalPool; }
    public void copyTo(BaseObjectPool other) { other.SetLocalPool(this.GetLocalPool()); }

    public static int GetSerializationCapacity() { return BaseObjectPool.SERIALIZATION_CAPACTITY; }
    public static float GetSerializationLoadFactor() { return BaseObjectPool.SERIALIZATION_LOAD_FACTOR; }
    public static int GetMaxCapacity() { return BaseObjectPool.MAX_CAPACTIY; }
    
    public int GetCurrentCapacity() { return this.GetLocalPool().size(); }
    
    private static void ValidateInput(String ID, String name, int amount, boolean NumberValidation) {
        Utils.CheckArgumentCondition(ID != null, "Object's ID cannot be null.");
        Utils.CheckArgumentCondition(name != null, "Object's name cannot be null.");
        if (NumberValidation) { Utils.CheckArgumentCondition(amount >= 0, "The amount must be zero or positive."); }
    }

    private static void ValidateInput(String ID, String name, int amount) {
        BaseObjectPool.ValidateInput(ID, name, amount, true);
    }

    public boolean IsEmpty() { return this.GetLocalPool().isEmpty(); }
    public boolean IsPoolHasExtraSlot() { return this.GetCurrentCapacity() < BaseObjectPool.GetMaxCapacity(); }
    public boolean IsPoolFull() { return !this.IsPoolHasExtraSlot(); }

    // ---------------------------------------------------------------------------------------------------------------------
    // TODO: Serialization & Deserialization
    public Hashtable<String, Object> Serialize() {
        Hashtable<String, Object> result = super.Serialize();
        Hashtable<String, Object> pool = this.GetLocalPool();
        Iterator<Entry<String, Object>> it = pool.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Object> entry = it.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            result.put(key, value);
        }
        return result;
    }

    public static BaseObjectPool Deserialize(Hashtable<String, Object> data) throws Exception {
        BaseObjectPool result = null;
        if (data.containsKey("ID")) {
            String ID = (String) data.get("ID");
            result = new BaseObjectPool(ID);
            Hashtable<String, Object> pool = result.GetLocalPool();
            Iterator<Entry<String, Object>> it = data.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, Object> entry = it.next();
                String key = entry.getKey();
                Object value = entry.getValue();
                pool.put(key, value);
            }
        } else {
            throw new Exception("BaseObjectPool.Deserialize: ID is not found.");
        }
        return result;

        
            
    



   
}
