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
package com.p5solutions.core.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

/**
 * The Class ModelState: Holds information about any type of formatting or
 * binding issue. This could potentially be used for non-json binding; for
 * example in a spring based form binding. However, in order to support this, we
 * need to start by looking at the {@link AnnotationMethodHandlerAdapter} and
 * making the necessary modifications to support this feature.
 * 
 * See {@link HttpServletRequest} or some sort of wrapper sub-class of
 * {@link WebRequest} by accessing attribute {@link ModelState#MODEL_STATE}
 * 
 * @author Kasra Rasaee
 * @since 2010-11-22
 * 
 * @see JsonDeserializer
 */
public class ModelState implements Serializable {

	/**
	 * The Class ModelError.
	 */
	public class ModelError {

		/** The binding path. */
		private String bindingPath;

		/** The error. */
		private String error;

		/** The exception. */
		private Throwable exception;

		/**
		 * Gets the binding path.
		 * 
		 * @return the binding path
		 */
		public String getBindingPath() {
			return bindingPath;
		}

		/**
		 * Sets the binding path.
		 * 
		 * @param bindingPath
		 *          the new binding path
		 */
		public void setBindingPath(String bindingPath) {
			this.bindingPath = bindingPath;
		}

		/**
		 * Gets the error.
		 * 
		 * @return the error
		 */
		public String getError() {
			return error;
		}

		/**
		 * Sets the error.
		 * 
		 * @param error
		 *          the new error
		 */
		public void setError(String error) {
			this.error = error;
		}

		/**
		 * Gets the exception.
		 * 
		 * @return the exception
		 */
		public Throwable getException() {
			return exception;
		}

		/**
		 * Sets the exception.
		 * 
		 * @param exception
		 *          the new exception
		 */
		public void setException(Throwable exception) {
			this.exception = exception;
		}
	}

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The MODE l_ state. */
	public static String MODEL_STATE = "MODEL_STATE_REQUEST_ATTRIBUTE_NAME";

	/** The errors. */
	private Map<String, ModelError> errors = null;

	/**
	 * Valid.
	 * 
	 * @return true, if successful
	 */
	public boolean valid() {
		if (this.errors != null && this.errors.size() > 0) {
			return false;
		}
		return true;
	}

	/**
	 * Get a model error by the binding path.
	 * 
	 * @param bindingPath
	 *          the binding path
	 * @return the boolean
	 */
	public ModelError get(String bindingPath) {
		if (this.errors != null) {
			return errors.get(bindingPath);
		}
		return null;
	}

	/**
	 * Gets all the errors in sequence of binding.
	 * 
	 * @return the all
	 */
	public List<ModelError> getAll() {
		if (this.errors != null) {
			return new ArrayList<ModelError>(this.errors.values());
		}
		return null;
	}

	/**
	 * Adds the.
	 * 
	 * @param bindingPath
	 *          the binding path
	 * @param error
	 *          the error
	 * @param exception
	 *          the exception
	 */
	public void add(String bindingPath, String error, Throwable exception) {
		ModelError me = new ModelError();
		me.setBindingPath(bindingPath);
		me.setError(error);
		me.setException(exception);
		add(me);
	}

	/**
	 * Adds the.
	 * 
	 * @param error
	 *          the error
	 */
	public void add(ModelError error) {
		if (this.errors == null) {
			this.errors = new HashMap<String, ModelError>();
		}
		errors.put(error.getBindingPath(), error);
	}
}