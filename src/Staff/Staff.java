package Staff;

import Person.Person;
import PrefixState.Prefix;

/**
 * Copyright (C) 2022-2022, HDM-Dev Team
 * All Rights Reserved

 * This file is part of HDM-Dev Team's project. The contents are
 * fully covered, controlled, and acknowledged by the terms of the
 * BSD-3 license, which is included in the file LICENSE.md, found
 * at the root of the project's source code/tree repository.
**/

/**
 * This is a dataclass which describes an instances of a normal staff, 
 * which have the privilege to access the second-layer internal system.
 * But in this case, the "internal system" is not developed.
 * 
 * @author Ichiru Take
 * @version 0.0.1
 * 
 * References:
 * 1) 
**/

public class Staff extends Person {
    
    public Staff(String ID, String name, String description) throws Exception {
        super(ID, name, description);
        this.prefix = Prefix.Staff;
    }

    public Staff(String ID, String name) throws Exception { this(ID, name, null); }


    // ----------------------------------------------------------
    public static Prefix GetPrefix() { return Prefix.Staff; }
    public static String GetPrefixCode() { return Staff.GetPrefix().GetPrefixCode(); }
    
}
