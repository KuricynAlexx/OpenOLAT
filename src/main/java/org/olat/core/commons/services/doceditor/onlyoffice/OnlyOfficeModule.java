/**
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * frentix GmbH, http://www.frentix.com
 * <p>
 */
package org.olat.core.commons.services.doceditor.onlyoffice;

import java.security.Key;

import org.apache.logging.log4j.Logger;
import org.olat.core.configuration.AbstractSpringModule;
import org.olat.core.configuration.ConfigOnOff;
import org.olat.core.logging.Tracing;
import org.olat.core.util.StringHelper;
import org.olat.core.util.coordinate.CoordinatorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.security.Keys;

/**
 * 
 * Initial date: 12 Apr 2019<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
@Service
public class OnlyOfficeModule extends AbstractSpringModule implements ConfigOnOff {

	private static final Logger log = Tracing.createLoggerFor(OnlyOfficeModule.class);
	
	private static final String ONLYOFFICE_ENABLED = "onlyoffice.enabled";
	private static final String ONLYOFFICE_BASE_URL = "onlyoffice.baseUrl";
	private static final String ONLYOFFICE_JWT_SECRET = "onlyoffice.jwt.secret";
	private static final String ONLYOFFICE_EDITOR_ENABLED = "onlyoffice.editor.enabled";
	private static final String ONLYOFFICE_LICENSE_EDIT = "onlyoffice.license.edit";
	private static final String ONLYOFFICE_DATA_TRANSER_CONFIRMATION_ENABLED = "onlyoffice.data.transfer.confirmation.enabled";
	private static final String ONLYOFFICE_USAGE_AUTHORS = "onlyoffice.usage.authors";
	private static final String ONLYOFFICE_USAGE_COACHES = "onlyoffice.usage.coaches";
	private static final String ONLYOFFICE_USAGE_MANAGERS = "onlyoffice.usage.managers";
	private static final String ONLYOFFICE_THUMBNAILS_ENABLED = "onlyoffice.thumbnails.enabled";
	
	@Value("${onlyoffice.enabled:false}")
	private boolean enabled;
	@Value("${onlyoffice.baseUrl}")
	private String baseUrl;
	@Value("${onlyoffice.api.path}")
	private String apiPath;
	private String apiUrl;
	@Value("${onlyoffice.conversion.path}")
	private String conversionPath;
	private String conversionUrl;
	private String jwtSecret;
	private Key jwtSignKey;
	@Value("${onlyoffice.editor.enabled:false}")
	private boolean editorEnabled;
	@Value("${onlyoffice.license.edit}")
	private Integer licenseEdit;
	@Value("${onlyoffice.data.transfer.confirmation.enabled:false}")
	private boolean dataTransferConfirmationEnabled;
	@Value("${onlyoffice.usage.restricted.authors:false}")
	private boolean usageRestrictedToAuthors;
	@Value("${onlyoffice.usage.restricted.coaches:false}")
	private boolean usageRestrictedToCoaches;
	@Value("${onlyoffice.usage.restricted.managers:false}")
	private boolean usageRestrictedToManagers;
	@Value("${onlyoffice.thumbnails.enabled:false}")
	private boolean thumbnailsEnabled;
	
	@Autowired
	private OnlyOfficeModule(CoordinatorManager coordinateManager) {
		super(coordinateManager);
	}

	@Override
	public void init() {
		updateProperties();
	}

	@Override
	protected void initFromChangedProperties() {
		updateProperties();
	}
	
	private void updateProperties() {
		String enabledObj = getStringPropertyValue(ONLYOFFICE_ENABLED, true);
		if(StringHelper.containsNonWhitespace(enabledObj)) {
			enabled = "true".equals(enabledObj);
		}
		
		String baseUrlObj = getStringPropertyValue(ONLYOFFICE_BASE_URL, true);
		if(StringHelper.containsNonWhitespace(baseUrlObj)) {
			baseUrl = baseUrlObj;
			resetApiUrls();
		}
		
		String jwtSecretObj = getStringPropertyValue(ONLYOFFICE_JWT_SECRET, true);
		if(StringHelper.containsNonWhitespace(jwtSecretObj)) {
			jwtSecret = jwtSecretObj;
		}
		
		String editorEnabledObj = getStringPropertyValue(ONLYOFFICE_EDITOR_ENABLED, true);
		if(StringHelper.containsNonWhitespace(editorEnabledObj)) {
			editorEnabled = "true".equals(editorEnabledObj);
		}
		
		String dataTransferConfirmationEnabledObj = getStringPropertyValue(ONLYOFFICE_DATA_TRANSER_CONFIRMATION_ENABLED, true);
		if(StringHelper.containsNonWhitespace(dataTransferConfirmationEnabledObj)) {
			dataTransferConfirmationEnabled = "true".equals(dataTransferConfirmationEnabledObj);
		}
		
		String licenseEditObj = getStringPropertyValue(ONLYOFFICE_LICENSE_EDIT, true);
		if(StringHelper.containsNonWhitespace(licenseEditObj)) {
			licenseEdit = getIntOrNull(licenseEditObj);
		}
		
		String usageRestrictedToAuthorsObj = getStringPropertyValue(ONLYOFFICE_USAGE_AUTHORS, true);
		if(StringHelper.containsNonWhitespace(usageRestrictedToAuthorsObj)) {
			usageRestrictedToAuthors = "true".equals(usageRestrictedToAuthorsObj);
		}
		
		String usageRestrictedToCoachesObj = getStringPropertyValue(ONLYOFFICE_USAGE_COACHES, true);
		if(StringHelper.containsNonWhitespace(usageRestrictedToCoachesObj)) {
			usageRestrictedToCoaches = "true".equals(usageRestrictedToCoachesObj);
		}
		
		String usageRestrictedToManagersObj = getStringPropertyValue(ONLYOFFICE_USAGE_MANAGERS, true);
		if(StringHelper.containsNonWhitespace(usageRestrictedToManagersObj)) {
			usageRestrictedToManagers = "true".equals(usageRestrictedToManagersObj);
		}
		
		String thumbnailsEnabledObj = getStringPropertyValue(ONLYOFFICE_THUMBNAILS_ENABLED, true);
		if(StringHelper.containsNonWhitespace(thumbnailsEnabledObj)) {
			thumbnailsEnabled = "true".equals(thumbnailsEnabledObj);
		}
	}
	
	private Integer getIntOrNull(String val) {
		try {
			return Integer.valueOf(val);
		} catch (Exception e) {
			//
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		setStringProperty(ONLYOFFICE_ENABLED, Boolean.toString(enabled), true);
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
		setStringProperty(ONLYOFFICE_BASE_URL, baseUrl, true);
		resetApiUrls();
	}
	
	public String getApiUrl() {
		return apiUrl;
	}
	
	public String getConversionUrl() {
		return conversionUrl;
	}
	
	private void resetApiUrls() {
		this.apiUrl = baseUrl + apiPath;
		this.conversionUrl = baseUrl + conversionPath;
	}
	
	public String getJwtSecret() {
		return jwtSecret;
	}

	public void setJwtSecret(String jwtSecret) {
		this.jwtSecret = jwtSecret;
		this.jwtSignKey = null;
		setStringProperty(ONLYOFFICE_JWT_SECRET, jwtSecret, true);
	}

	public Key getJwtSignKey() {
		if (jwtSignKey == null) {
			try {
				jwtSignKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
			} catch (Exception e) {
				log.error("", e);
			}
		}
		return jwtSignKey;
	}
	
	public boolean isEditorEnabled() {
		return editorEnabled;
	}

	public void setEditorEnabled(boolean editorEnabled) {
		this.editorEnabled = enabled;
		setStringProperty(ONLYOFFICE_EDITOR_ENABLED, Boolean.toString(editorEnabled), true);
	}

	public Integer getLicenseEdit() {
		return licenseEdit;
	}

	public void setLicenseEdit(Integer licenseEdit) {
		this.licenseEdit = licenseEdit;
		setStringProperty(ONLYOFFICE_LICENSE_EDIT, licenseEdit == null? null: licenseEdit.toString(), true);
	}

	public boolean isDataTransferConfirmationEnabled() {
		return dataTransferConfirmationEnabled;
	}

	public void setDataTransferConfirmationEnabled(boolean dataTransferConfirmationEnabled) {
		this.dataTransferConfirmationEnabled = dataTransferConfirmationEnabled;
		setStringProperty(ONLYOFFICE_DATA_TRANSER_CONFIRMATION_ENABLED, Boolean.toString(dataTransferConfirmationEnabled), true);
	}

	public boolean isUsageRestricted() {
		return usageRestrictedToAuthors || usageRestrictedToCoaches || usageRestrictedToManagers;
	}

	public boolean isUsageRestrictedToAuthors() {
		return usageRestrictedToAuthors;
	}

	public void setUsageRestrictedToAuthors(boolean usageRestrictedToAuthors) {
		this.usageRestrictedToAuthors = usageRestrictedToAuthors;
		setStringProperty(ONLYOFFICE_USAGE_AUTHORS, Boolean.toString(usageRestrictedToAuthors), true);
	}

	public boolean isUsageRestrictedToCoaches() {
		return usageRestrictedToCoaches;
	}

	public void setUsageRestrictedToCoaches(boolean usageRestrictedToCoaches) {
		this.usageRestrictedToCoaches = usageRestrictedToCoaches;
		setStringProperty(ONLYOFFICE_USAGE_COACHES, Boolean.toString(usageRestrictedToCoaches), true);
	}

	public boolean isUsageRestrictedToManagers() {
		return usageRestrictedToManagers;
	}

	public void setUsageRestrictedToManagers(boolean usageRestrictedToManagers) {
		this.usageRestrictedToManagers = usageRestrictedToManagers;
		setStringProperty(ONLYOFFICE_USAGE_MANAGERS, Boolean.toString(usageRestrictedToManagers), true);
	}
	
	public boolean isThumbnailsEnabled() {
		return thumbnailsEnabled;
	}

	public void setThumbnailsEnabled(boolean thumbnailsEnabled) {
		this.thumbnailsEnabled = thumbnailsEnabled;
		setStringProperty(ONLYOFFICE_THUMBNAILS_ENABLED, Boolean.toString(thumbnailsEnabled), true);
	}

}
