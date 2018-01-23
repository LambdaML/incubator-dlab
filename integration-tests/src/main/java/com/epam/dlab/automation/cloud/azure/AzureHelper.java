package com.epam.dlab.automation.cloud.azure;

import com.epam.dlab.automation.cloud.CloudException;
import com.epam.dlab.automation.helper.ConfigPropertyValue;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.PowerState;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.network.PublicIPAddress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class AzureHelper{

    private static final Logger LOGGER = LogManager.getLogger(AzureHelper.class);
    private static final Duration CHECK_TIMEOUT = Duration.parse("PT10m");
    private static final String LOCALHOST_IP = ConfigPropertyValue.get("LOCALHOST_IP");

    private static Azure azure;

    static {
        try {
            azure = Azure.configure().authenticate(new File(ConfigPropertyValue.getAzureAuthFileName())).withDefaultSubscription();
        } catch (IOException e) {
            LOGGER.warn("Exception is occured ", e);
        }
    }

    private AzureHelper(){}

    private static List<VirtualMachine> getVirtualMachines(){
        return !azure.virtualMachines().list().isEmpty() ? new ArrayList<>(azure.virtualMachines().list()) : null;
    }

    public static List<VirtualMachine> getVirtualMachinesByTag(String tag, boolean restrictionMode){
        if(ConfigPropertyValue.isRunModeLocal()){

            List<VirtualMachine> vmLocalModeList = new ArrayList<>();
            VirtualMachine mockedVM = mock(VirtualMachine.class);
            PublicIPAddress mockedIPAddress = mock(PublicIPAddress.class);
            NetworkInterface mockedNetworkInterface = mock(NetworkInterface.class);
            when(mockedVM.getPrimaryPublicIPAddress()).thenReturn(mockedIPAddress);
            when(mockedIPAddress.ipAddress()).thenReturn(LOCALHOST_IP);
            when(mockedVM.getPrimaryNetworkInterface()).thenReturn(mockedNetworkInterface);
            when(mockedNetworkInterface.primaryPrivateIP()).thenReturn(LOCALHOST_IP);
            vmLocalModeList.add(mockedVM);

            return vmLocalModeList;

        }
        List<VirtualMachine> vmList = getVirtualMachines();
        if(vmList == null){
            LOGGER.warn("There is not any virtual machine in Azure");
            return vmList;
        }
        if(restrictionMode){
            vmList.removeIf(vm -> !containsTag(vm, tag));
        }
        else{
            vmList.removeIf(vm -> !containsSimilarTag(vm, tag));
        }
        return !vmList.isEmpty() ? vmList : null;
    }

    private static boolean containsTag(VirtualMachine vm, String tag){
        return vm.tags().containsValue(tag);
    }

    private static boolean containsSimilarTag(VirtualMachine vm, String tag){
        List<String> tags = new ArrayList<>(vm.tags().values());
        for(String tagValue : tags){
            if(tagValue.contains(tag)){
                return true;
            }
        }
        return false;
    }

    private static PowerState getStatus(VirtualMachine vm){
        return vm.powerState();
    }

    public static void checkAzureStatus(String virtualMachineTag, PowerState expAzureState, boolean restrictionMode) throws CloudException, InterruptedException {
        LOGGER.info("Check status of virtual machine with tag {} on Azure", virtualMachineTag);
        if (ConfigPropertyValue.isRunModeLocal()) {
            LOGGER.info("Azure virtual machine with tag {} fake state is {}", virtualMachineTag, expAzureState);
            return;
        }
        List<VirtualMachine> vmsWithTag = getVirtualMachinesByTag(virtualMachineTag, restrictionMode);
        if(vmsWithTag == null){
            LOGGER.warn("There is not any virtual machine in Azure with tag {}", virtualMachineTag);
            return;
        }

        PowerState virtualMachineState;
        long requestTimeout = ConfigPropertyValue.getAzureRequestTimeout().toMillis();
        long timeout = CHECK_TIMEOUT.toMillis();
        long expiredTime = System.currentTimeMillis() + timeout;
        VirtualMachine virtualMachine = vmsWithTag.get(0);
        while (true) {
            virtualMachineState = getStatus(virtualMachine);
            if (virtualMachineState == expAzureState) {
                break;
            }
            if (timeout != 0 && expiredTime < System.currentTimeMillis()) {
                LOGGER.info("Azure virtual machine with tag {} state is {}", virtualMachineTag, getStatus(virtualMachine));
                throw new CloudException("Timeout has been expired for check state of azure virtual machine with tag " + virtualMachineTag);
            }
            Thread.sleep(requestTimeout);
        }

        for (VirtualMachine  vm : vmsWithTag) {
            LOGGER.info("Azure virtual machine with tag {} state is {}. Virtual machine id {}, private IP {}, public IP {}",
                    virtualMachineTag, getStatus(vm), vm.vmId(), vm.getPrimaryNetworkInterface().primaryPrivateIP(),
                    vm.getPrimaryPublicIPAddress() != null ? vm.getPrimaryPublicIPAddress().ipAddress() : "doesn't exist for this resource type");
        }
        Assert.assertEquals(virtualMachineState, expAzureState, "Azure virtual machine with tag " + virtualMachineTag +
                " state is not correct. Virtual machine id " +
                virtualMachine.vmId() + ", private IP " + virtualMachine.getPrimaryNetworkInterface().primaryPrivateIP() +
                ", public IP " +
                (virtualMachine.getPrimaryPublicIPAddress() != null ? virtualMachine.getPrimaryPublicIPAddress().ipAddress() : "doesn't exist for this resource type" ));
    }

}
