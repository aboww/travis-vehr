/*
 * Copyright (c) 2015 Christian Chevalley
 * This file is part of Project Ethercis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
This code is a refactoring and adaptation of the original
work provided by the XmlBlaster project (see http://xmlblaster.org)
for more details.
This code is therefore supplied under LGPL 2.1
 */

/**
 * Project: EtherCIS system application
 * 
 * @author <a href="mailto:christian@adoc.co.th">Christian Chevalley</a>
 * @author <a href="mailto:michele@laghi.eu">Michele Laghi</a>
 * @author <a href="mailto:xmlblast@marcelruff.info">Marcel Ruff</a>
 */


package com.ethercis.servicemanager.common.property;

public final class PropInt extends PropEntry implements java.io.Serializable, Cloneable
{
   /**
	 * 
	 */
	private static final long serialVersionUID = -5008633777373992622L;
	private int value;

   /**
    * Constructor for the default value
    */
   public PropInt(int value) {
      super(null);
      this.value = value;
   }

   /**
    * Constructor for the default value
    */
   public PropInt(String propName, int value) {
      super(propName);
      this.value = value;
   }

   /**
    * @return "int"
    */
   public final String getTypeString() {
      return "int";
   }

   /**
    * @return The value in String form
    */
   public final String getValueString() {
      return ""+this.value;
   }

   public void setValue(int value) {
      this.value = value;
      super.creationOrigin = CREATED_BY_SETTER;
   }

   /**
    * @param The new value logonservice String type, will be converted to native type
    * @param creationOrigin e.g. PropEntry.CREATED_BY_JVMENV
    */
   public void setValue(String value, int creationOrigin) {
      if (value == null) return;
      setValue(Integer.parseInt(value), creationOrigin);
   }

   /**
    * @param creationOrigin e.g. PropEntry.CREATED_BY_JVMENV
    */
   public void setValue(int value, int creationOrigin) {
      this.value = value;
      super.creationOrigin = creationOrigin;
   }

   public int getValue() {
      return this.value;
   }

   /**
    * Returns a shallow clone, you can change safely all basic or immutable types
    * like boolean, String, int.
    * Currently RouteInfo is not cloned (so don't change it)
    */
   public Object clone() {
      return super.clone();
   }


}
