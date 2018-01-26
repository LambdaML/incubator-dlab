/***************************************************************************

Copyright (c) 2016, EPAM SYSTEMS INC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

****************************************************************************/

package com.epam.dlab.automation.helper;

import com.epam.dlab.automation.cloud.CloudException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class NamingHelper {
    private static AtomicInteger idCounter = new AtomicInteger(0);
    
    private static String serviceBaseName;
    private static String ssnURL;
    private static String ssnIp;
    private static String ssnToken;

    private NamingHelper(){}
    
    public static String getServiceBaseName() {
    	return serviceBaseName;
    }
    
    public static void setServiceBaseName(String serviceBaseName) {
    	if (NamingHelper.serviceBaseName != null) {
    		throw new IllegalArgumentException("Field serviceBaseName already has a value");
    	}
    	NamingHelper.serviceBaseName = serviceBaseName;
    }
    
    public static String getSsnURL() {
    	return ssnURL;
    }
    
    public static void setSsnURL(String ssnURL) {
    	if (NamingHelper.ssnURL != null) {
    		throw new IllegalArgumentException("Field ssnURL already has a value");
    	}
    	NamingHelper.ssnURL = ssnURL;
    }

    public static String getSsnName() {
    	return serviceBaseName + "-ssn";
    }
    
    public static String getSsnIp() {
    	return ssnIp;
    }
    
    public static void setSsnIp(String ssnIp) {
    	if (NamingHelper.ssnIp != null) {
    		throw new IllegalArgumentException("Field ssnIp already has a value");
    	}
    	NamingHelper.ssnIp = ssnIp;
    }

    public static String getSsnToken() {
    	return ssnToken;
    }
    
    public static void setSsnToken(String ssnToken) {
    	if (NamingHelper.ssnToken != null) {
    		throw new IllegalArgumentException("Field ssnToken already has a value");
    	}
    	NamingHelper.ssnToken = ssnToken;
    }
    
    public static String getEdgeName() {
        switch (ConfigPropertyValue.getCloudProvider()) {
            case CloudProvider.AWS_PROVIDER:
                return String.join("-", serviceBaseName, ConfigPropertyValue.getUsernameSimple(), "edge");
            case CloudProvider.AZURE_PROVIDER:
                return String.join("-", serviceBaseName, ConfigPropertyValue.getUsernameSimple(), "edge")
                        .replace('_', '-');
            default:
                return null;
        }
    }
    
    public static String getNotebookInstanceName(String notebookName) {
        switch (ConfigPropertyValue.getCloudProvider()) {
            case CloudProvider.AWS_PROVIDER:
                return String.join("-", serviceBaseName, ConfigPropertyValue.getUsernameSimple(), "nb", notebookName);
            case CloudProvider.AZURE_PROVIDER:
                return String.join("-", serviceBaseName, ConfigPropertyValue.getUsernameSimple(), "nb", notebookName)
                        .replace('_', '-');
            default:
                return null;
        }
    }
    
    public static String getClusterInstanceName(String notebookName, String clusterName, String dataEngineType) {
        if("dataengine".equals(dataEngineType)) {
            switch (ConfigPropertyValue.getCloudProvider()) {
                case CloudProvider.AWS_PROVIDER:
                    return String.join("-", getClusterInstanceNameForTestDES(notebookName,clusterName,dataEngineType), "m");
                case CloudProvider.AZURE_PROVIDER:
                    return String.join("-", getClusterInstanceNameForTestDES(notebookName,clusterName,dataEngineType), "m")
                            .replace('_', '-');
                default:
                    return null;
            }
    	}
    	else {
    		return getClusterInstanceNameForTestDES(notebookName,clusterName,dataEngineType);
    	}
    }
    
    public static String getClusterInstanceNameForTestDES(String notebookName, String clusterName, String dataEngineType) {
        switch (ConfigPropertyValue.getCloudProvider()) {
            case CloudProvider.AWS_PROVIDER:
                if("dataengine".equals(dataEngineType)) {
                    return String.join("-", serviceBaseName, ConfigPropertyValue.getUsernameSimple(), "de", notebookName, clusterName);
                }
                else {
                    return String.join("-", serviceBaseName, ConfigPropertyValue.getUsernameSimple(), "des", notebookName, clusterName);
                }
            case CloudProvider.AZURE_PROVIDER:
                if("dataengine".equals(dataEngineType)) {
                    return String.join("-", serviceBaseName, ConfigPropertyValue.getUsernameSimple(), "de", notebookName, clusterName)
                            .replace('_', '-');
                }
                else {
                    return String.join("-", serviceBaseName, ConfigPropertyValue.getUsernameSimple(), "des", notebookName, clusterName)
                            .replace('_', '-');
                }
            default:
                return null;
        }

    }

    public static String getNotebookContainerName(String notebookName, String action) {
    	return String.join("_", ConfigPropertyValue.getUsernameSimple(), action, "exploratory", notebookName);
    }
    
    public static String getClusterContainerName(String clusterName, String action) {
    	return String.join("_", ConfigPropertyValue.getUsernameSimple(), action, "computational", clusterName);
    }
    
    
    public static String generateRandomValue() {
        SimpleDateFormat df = new SimpleDateFormat("YYYYMMddhmmss");
        return String.join("_",  "ITest", df.format(new Date()), String.valueOf(idCounter.incrementAndGet()));
    }

    public static String generateRandomValue(String notebokTemplateName) {
        return String.join("_", notebokTemplateName, String.valueOf(idCounter.incrementAndGet()));
    }
    
    public static String getSelfServiceURL(String path) {
        return ssnURL + path;
    }
    
    public static String getStorageName() {
        switch (ConfigPropertyValue.getCloudProvider()) {
            case CloudProvider.AWS_PROVIDER:
                return String.format("%s-%s-%s", serviceBaseName, ConfigPropertyValue.getUsernameSimple(),
                        CloudHelper.getStorageNameAppendix()).replace('_', '-').toLowerCase();
            case CloudProvider.AZURE_PROVIDER:
                return String.format("%s-%s-%s", serviceBaseName, "shared",
                        CloudHelper.getStorageNameAppendix()).replace('_', '-').toLowerCase();
            default:
                return null;
        }
    }
    
    public static String getClusterName(String clusterInstanceName, boolean restrictionMode) throws CloudException {
        return CloudHelper.getInstanceNameByCondition(clusterInstanceName, restrictionMode);
    }

    public static String getClusterName(String clusterInstanceName, String dataEngineType, boolean restrictionMode) throws CloudException {
        if ("dataengine".equals(dataEngineType)) {
            return clusterInstanceName;
        } else {
            return CloudHelper.getInstanceNameByCondition(clusterInstanceName, restrictionMode);
        }
    }

    public static String getNotebookTestTemplatesPath(String notebookName){
        if(notebookName.contains("deeplearning")){
            return "test_templates/deeplearning/";
        }
        else if(notebookName.contains("jupyter")){
            return "test_templates/jupyter/";
        }
        else if(notebookName.contains("rstudio")){
            return "test_templates/rstudio/";
        }
        else if(notebookName.contains("tensor")){
            return "test_templates/tensor/";
        }
        else if(notebookName.contains("zeppelin")){
            return "test_templates/zeppelin/";
        }
        else return "";

    }

    public static String getNotebookType(String notebookName){
        if(notebookName.contains("deeplearning")){
            return "deeplearning/";
        }
        else if(notebookName.contains("jupyter")){
            return "jupyter/";
        }
        else if(notebookName.contains("rstudio")){
            return "rstudio/";
        }
        else if(notebookName.contains("tensor")){
            return "tensor/";
        }
        else if(notebookName.contains("zeppelin")){
            return "zeppelin/";
        }
        else return "";

    }


}