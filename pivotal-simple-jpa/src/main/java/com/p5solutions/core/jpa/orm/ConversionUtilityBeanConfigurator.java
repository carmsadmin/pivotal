package com.p5solutions.core.jpa.orm;

import org.springframework.core.convert.ConversionService;

import com.p5solutions.core.jpa.orm.transaction.TransactionTemplate;

/**
 * The Class ConversionUtilityBeanConfigurator. Fixes circular reference issues
 * where {@link ConversionUtility} depends on {@link ConversionService} and at
 * some stage the an implementation of {@link ConversionService} depends back on
 * {@link TransactionTemplate} and ultimately dependent on an instance of
 * {@link ConversionUtility}
 * 
 * @author Kasra Rasaee
 * @since 20121129
 */
public class ConversionUtilityBeanConfigurator {

  /** The conversion service. */
  private ConversionService conversionService;

  /** The conversion utility. */
  private ConversionUtility conversionUtility;

  /**
   * Instantiates a new conversion utility bean configurator.
   */
  public ConversionUtilityBeanConfigurator() {
    super();
  }

  /**
   * Initialize.
   */
  public void initialize() {
    conversionUtility.setConversionService(conversionService);
  }

  /**
   * Gets the conversion utility.
   * 
   * @return the conversion utility
   */
  public ConversionUtility getConversionUtility() {
    return conversionUtility;
  }

  /**
   * Sets the conversion utility.
   * 
   * @param conversionUtility
   *          the new conversion utility
   */
  public void setConversionUtility(ConversionUtility conversionUtility) {
    this.conversionUtility = conversionUtility;
  }

  /**
   * Gets the conversion service.
   * 
   * @return the conversion service
   */
  public ConversionService getConversionService() {
    return conversionService;
  }

  /**
   * Sets the conversion service.
   * 
   * @param conversionService
   *          the new conversion service
   */
  public void setConversionService(ConversionService conversionService) {
    this.conversionService = conversionService;
  }

}
