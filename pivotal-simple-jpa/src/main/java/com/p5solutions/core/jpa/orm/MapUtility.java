/* Pivotal 5 Solutions Inc. - Core Java library for all other Pivotal Java Modules.
 * 
 * Copyright (C) 2011  KASRA RASAEE
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package com.p5solutions.core.jpa.orm;

/**
 * The Class MapUtility. Interface defining the structure of the
 * {@link MapUtility} implementation.
 * 
 * @author Kasra Rasaee
 * @since 2010-11-10
 * 
 * @see MapUtilityImpl
 */
public interface MapUtility {

  /**
   * Try to map the value to the path, recursively, starting with the target
   * object as the root.
   * 
   * <pre>
   * ## EXAMPLE ## 
   * | target = root instance of "entity" see below
   * | value = the value to be set to firstName
   * 
   * STEP 1: 
   *  path = 'entity.extraInfo.person.firstName'
   *    > p[0] = 'entity'
   *    > p[1] = "extraInfo.person.firstName'
   * 
   * STEP 2:
   *  Recursion, call the next path, which will result in something like
   *  path = 'extraInfo.person.firstName'
   *    > p[0] = 'extraInfo';
   *    > p[1] = 'person.firstName'
   * </pre>
   * 
   * @param pb
   *          the ParameterBinder for the given target method
   * 
   *          <pre>
   * 	Example: if bindingPath = person.details.birthDate , then the pb would be for birthDate and not for person or details.
   * </pre>
   * 
   * @param target
   *          the target
   * @param value
   *          the value
   * @param bindingPath
   *          the binding path
   * @return the object
   */
  public abstract Object map(ParameterBinder pb, Object target, Object value, String bindingPath);

  /**
   * Attempt to get a value from the target object by the given binding path.
   * 
   * <pre>
   * ## EXAMPLE ##
   * Class (Deep)
   *  -> Getter/Setter - public Object getValue(); setValue(....) { }
   *  -> Getter/Setter - public Date getSomeDate(); setSomeDate(....) { }
   * Class (Embedded)
   *  -> Getter/Setter - public Deep getDeepObject(); setDeepObject(...) { }
   *  -> Getter/Setter - public String getSomeText(); setSomeText(...) { } 
   *  -> Getter/Setter - public Integer getSomeInteger(); setSomeInteger(...) { }
   * Class (Root)
   *  -> Getter/Setter - public Embedded getEmbedded(); setEmbedded(Embedded emb) {..}
   * 
   * ## THEN ##
   *  Date dt = (Date)get(<instance_of_root>, "embedded.deepObject.someDate");
   * or
   *  String txt = (String)get(<instance_of_root>, "embedded.someText");
   * or 
   *  Deep deep  = (Deep)get(<instance_of_root>, "embedded.deepObject");
   * </pre>
   * 
   * @param pb
   *          the ParameterBinder for the given target method
   * 
   *          <pre>
   * 	Example: if bindingPath = person.details.birthDate , then the pb would be for birthDate and not for person or details.
   * </pre>
   * 
   * @param target
   *          the target
   * @param bindingPath
   *          the binding path
   * @return the object
   */
  public abstract Object get(ParameterBinder pb, Object target, String bindingPath);
}