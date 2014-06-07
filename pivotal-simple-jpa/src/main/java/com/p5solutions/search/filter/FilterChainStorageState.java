package com.p5solutions.search.filter;

import javax.persistence.Transient;
import java.io.Serializable;

/**
 * @author: smin
 * @Date: 10/27/13
 * @Time: 6:16 PM
 */
public interface FilterChainStorageState  extends Serializable {
    Long getChainId();

    /**
     * Sets the search criteria id.
     *
     * @param chainId
     *          the new search criteria id
     */
    void setChainId(Long chainId);

    /**
     * Gets the name.
     *
     * @return the name
     */
    String getName();

    /**
     * Sets the name.
     *
     * @param name
     *          the new name
     */
    void setName(String name);


    /**
     * Gets locale resolved description based on the {@link org.springframework.context.i18n.LocaleContextHolder}.
     *
     * @return the resolved description
     */
    @Transient
    String getResolvedDescription();

    /**
     * Gets the state data.
     *
     * @return the state data
     */
    String getStateData();

    /**
     * Sets the state data.
     *
     * @param stateData
     *          the new state data
     */
    void setStateData(String stateData);
}
