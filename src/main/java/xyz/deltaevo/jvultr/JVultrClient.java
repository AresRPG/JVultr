/*
 * Copyright 2015 DeltaEvolution
 *
 * This file is part of JVultr.
 * JVultr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JVultr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JVultr. If not, see <http://www.gnu.org/licenses/>.
 */
package xyz.deltaevo.jvultr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import xyz.deltaevo.jvultr.annotation.Optional;
import xyz.deltaevo.jvultr.api.*;
import xyz.deltaevo.jvultr.exception.JVultrException;

import java.util.*;

/**
 * A class to communicate with Vultr API
 * @author DeltaEvultion
 */
public class JVultrClient {

    /**
     * The Vultr API Key
     */
    private String apiKey;

    /**
     * Create a new JVultrClient Instance to communicate with Vultr API
     * @param apiKey the JVultr apiKey available in vultr Members Area
     * <p><a href="https://my.vultr.com/settings/#API" target="_blank">Vultr API Doc</a></p>
     */
    public JVultrClient(String apiKey){
        this.apiKey = apiKey;
    }

    /**
     * Retrieve information about the current account
     * <p><a href="https://www.vultr.com/api/#account_info" target="_blank">Vultr API Doc</a></p>
     * @return the account info
     * @throws JVultrException if an error Occurred
     * @see JVultrAccountInfo
     */
    public JVultrAccountInfo getAccountInfo() throws JVultrException{
        JsonParser parser = new JsonParser();
        JsonElement response = parser.parse(JVultrAPI.get(JVultrAPI.ENDPOINT + "v1/account/info" ,apiKey));
        if(response.isJsonObject())return new JVultrAccountInfo((JsonObject) response);
        else return null;
    }

    /**
     * List all snapshots on the current account
     * <p><a href="https://www.vultr.com/api/#snapshot_snapshot_list" target="_blank">Vultr API Doc</a></p>
     * @return an HashMap with the Vultr Snapshot key and the JVultrSnapshot
     * @throws JVultrException if an error Occurred
     * @see JVultrSnapshot
     */
    public HashMap<String , JVultrSnapshot> getSnapshots() throws JVultrException {
        JsonParser parser = new JsonParser();
        JsonElement response = parser.parse(JVultrAPI.get(JVultrAPI.ENDPOINT + "v1/snapshot/list" ,apiKey));
        if(response.isJsonObject()){
            HashMap<String , JVultrSnapshot> snapshots = new HashMap<>();
            for(Map.Entry<String , JsonElement> element : ((JsonObject)response).entrySet()){
                if(element.getValue().isJsonObject())
                    snapshots.put(element.getKey() , new JVultrSnapshot((JsonObject)element.getValue()));
            }
            return snapshots;
        }
        return new HashMap<>();
    }

    /**
     * List all ISOs currently available on this account
     * <p><a href="https://www.vultr.com/api/#iso_iso_list" target="_blank">Vultr API Doc</a></p>
     * @return HashMap with the Vultr ISO key and the JVultrISO
     * @throws JVultrException if an error Occurred
     * @see JVultrISO
     */
    public HashMap<Integer , JVultrISO> getISOs() throws JVultrException {
        JsonParser parser = new JsonParser();
        JsonElement response = parser.parse(JVultrAPI.get(JVultrAPI.ENDPOINT + "v1/iso/list" ,apiKey));
        if(response.isJsonObject()){
            HashMap<Integer , JVultrISO> isos = new HashMap<>();
            for(Map.Entry<String , JsonElement> element : ((JsonObject)response).entrySet()){
                if(element.getValue().isJsonObject())
                    isos.put(Integer.parseInt(element.getKey()) , new JVultrISO((JsonObject)element.getValue()));
            }
            return isos;
        }
        return new HashMap<>();
    }

    /**
     * List all ISOs currently available on this account
     * <p><a href="https://www.vultr.com/api/#iso_iso_list" target="_blank">Vultr API Doc</a></p>
     * @return HashMap with the Vultr Script key and the JVultrScript
     * @throws JVultrException if an error Occurred
     * @see JVultrScript
     */
    public HashMap<Integer , JVultrScript> getScripts() throws JVultrException {
        JsonParser parser = new JsonParser();
        JsonElement response = parser.parse(JVultrAPI.get(JVultrAPI.ENDPOINT + "v1/startupscript/list",apiKey));
        if(response.isJsonObject()){
            HashMap<Integer , JVultrScript> scripts = new HashMap<>();
            for(Map.Entry<String , JsonElement> element : ((JsonObject)response).entrySet()){
                if(element.getValue().isJsonObject())
                    scripts.put(Integer.parseInt(element.getKey()) , new JVultrScript((JsonObject)element.getValue()));
            }
            return scripts;
        }
        return new HashMap<>();
    }

    /**
     * Delete script represented by this id
     * <p><a href="https://www.vultr.com/api/#startupscript_destroy" target="_blank">Vultr API Doc</a></p>
     * @param id Vultr script id
     * @throws JVultrException if an error Occurred
     */
    public void destroyScript(int id) throws JVultrException{
        HashMap<String , Object> params = new HashMap<>();
        params.put("SCRIPTID" , id);
        JVultrAPI.post(JVultrAPI.ENDPOINT + "v1/startupscript/destroy",apiKey , params);
    }

    /**
     * Create new startup script
     * <p><a href="https://www.vultr.com/api/#startupscript_create" target="_blank">Vultr API Doc</a></p>
     * @param name Script name
     * @param script Script content
     * @param type Script type
     * @return JVultrScript representing this new Script
     * @throws JVultrException if an error Occurred
     */
    public JVultrScript createScript(String name ,String script , JVultrScript.Type type) throws JVultrException{
        HashMap<String , Object> params = new HashMap<>();
        params.put("name" , name);
        params.put("script" , script);
        params.put("type" ,type.name().toLowerCase());
        JsonElement response = new JsonParser().parse(JVultrAPI.post(JVultrAPI.ENDPOINT + "v1/startupscript/create" ,apiKey , params));
        if(response.isJsonObject()){
            return new JVultrScript(((JsonObject)response).get("SCRIPTID").getAsInt() , new Date() , new Date() , name , type , script);
        }else return null;
    }

    /**
     * Update existing startup script
     * <p><a href="https://www.vultr.com/api/#startupscript_update" target="_blank">Vultr API Doc</a></p>
     * @param id id of the Vultr startup script
     * @param name if not null new name for the script
     * @param script if not null new content of the script
     * @throws JVultrException if an error Occurred
     */
    public void updateScript(int id , @Optional String name , @Optional String script) throws JVultrException{
        HashMap<String , Object> params = new HashMap<>();
        params.put("SCRIPTID" , id);
        if(name != null)params.put("name" , name);
        if(script != null)params.put("script" , script);
        JVultrAPI.post(JVultrAPI.ENDPOINT + "v1/startupscript/destroy",apiKey, params);
    }

    /**
     * Update an existing startup script
     * <p><a href="https://www.vultr.com/api/#startupscript_update" target="_blank">Vultr API Doc</a></p>
     * @param script script to be updated
     * @throws JVultrException if an error Occurred
     * @see JVultrScript
     */
    public void updateScript(JVultrScript script) throws JVultrException{
        updateScript(script.getId() , script.getName() , script.getScript());
        script.setModified(new Date());
    }

    /**
     * Retrieves a list of operating systems to which this server can be changed.
     * <p><a href="https://www.vultr.com/api/#server_os_change_list" target="_blank">Vultr API Doc</a></p>
     * @return HashMap with the Vultr OS id and the JVultrOS
     * @throws JVultrException if an error Occurred
     * @see JVultrScript
     */
    public HashMap<String , JVultrOS> getOsChangeListFor(JVultrServer server) throws JVultrException {
        JsonParser parser = new JsonParser();
        JsonElement response = parser.parse(JVultrAPI.get(JVultrAPI.ENDPOINT + "v1/server/os_change_list?SUBID=" + server.getId() ,apiKey));
        if(response.isJsonObject()){
            HashMap<String , JVultrOS> oss = new HashMap<>();
            for(Map.Entry<String , JsonElement> element : ((JsonObject)response).entrySet()){
                if(element.getValue().isJsonObject())
                    oss.put(element.getKey() , new JVultrOS((JsonObject)element.getValue()));
            }
            return oss;
        }
        return new HashMap<>();
    }

    /**
     * Retrieve a list of all active plan
     * <p>Use this method only if you have special plans available</p>
     * <p><a href="https://www.vultr.com/api/#plans_plan_list" target="_blank">Vultr API Doc</a></p>
     * @return HashMap with the Vultr Plan id and the JVultrPlan
     * @throws JVultrException if an error Occurred
     * @see JVultrPlan
     * @see JVultrAPI#getPlans()
     */
    public HashMap<Integer , JVultrPlan> getPlans() throws JVultrException{
        JsonElement response = new JsonParser().parse(JVultrAPI.get(JVultrAPI.ENDPOINT + "v1/plans/list" ,apiKey));
        if(response.isJsonObject()){
            HashMap<Integer , JVultrPlan> os = new HashMap<>();
            for(Map.Entry<String , JsonElement> element : ((JsonObject)response).entrySet()){
                if(element.getValue().isJsonObject())
                    os.put(Integer.parseInt(element.getKey()) , new JVultrPlan((JsonObject)element.getValue()));
            }
            return os;
        }
        return new HashMap<>();
    }

    /**
     * List all active or pending virtual machines on the current account.
     * <p><a href="https://www.vultr.com/api/#server_server_list" target="_blank">Vultr API Doc</a></p>
     * @return HashMap with the Vultr Server id and the JVultrServer
     * @throws JVultrException if an error Occurred
     * @see JVultrServer
     */
    public HashMap<Integer , JVultrServer> getSevers() throws JVultrException {
        JsonElement response = new JsonParser().parse(JVultrAPI.get(JVultrAPI.ENDPOINT + "v1/server/list",apiKey));
        if(response.isJsonObject()){
            HashMap<Integer , JVultrServer> snapshots = new HashMap<>();
            for(Map.Entry<String , JsonElement> element : ((JsonObject)response).entrySet()){
                if(element.getValue().isJsonObject())
                    snapshots.put(Integer.parseInt(element.getKey()) , new JVultrServer((JsonObject)element.getValue()));
            }
            return snapshots;
        }
        return new HashMap<>();
    }

    /**
     * Retrieves the user data for this server.
     * <p><a href="https://www.vultr.com/api/#server_get_user_data" target="_blank">Vultr API Doc</a></p>
     * @return JVultrUserData user data of this server
     * @throws JVultrException if an error Occurred
     * @see JVultrUserData
     */
    public JVultrUserData getUserData(int server) throws JVultrException{
        JsonElement response = new JsonParser().parse(JVultrAPI.get(JVultrAPI.ENDPOINT + "v1/server/get_user_data?SUBID="+server,apiKey));
        if(response.isJsonObject())return new JVultrUserData((JsonObject) response);
        return null;
    }

    /**
     * List all domains associated with the current account
     * <p><a href="https://www.vultr.com/api/#dns_dns_list" target="_blank">Vultr API Doc</a></p>
     * @return List with all account JVultrDns
     * @throws JVultrException if an error Occurred
     * @see JVultrDns
     */
    public List<JVultrDns> getDNSs() throws JVultrException{
        JsonElement response = new JsonParser().parse(JVultrAPI.get(JVultrAPI.ENDPOINT + "v1/dns/list",apiKey));
        if(response.isJsonArray()){
            List<JVultrDns> dnss = new ArrayList<>();
            for(JsonElement element : response.getAsJsonArray()){
                if(element.isJsonObject())dnss.add(new JVultrDns((JsonObject) element));
            }
            return dnss;
        }
        return new ArrayList<>();
    }

    public List<JVultrPlan> getUpgradePlanList(int serverId) throws JVultrException{
        JsonElement response = new JsonParser().parse(JVultrAPI.get(JVultrAPI.ENDPOINT + "v1/server/upgrade_plan_list?SUBID=" + serverId,apiKey));
        if(response.isJsonArray()){
            List<JVultrPlan> servers = new ArrayList<>();
            for(JsonElement element : response.getAsJsonArray()){
                servers.add(JVultrCache.getCachedPlan(element.getAsInt()));
            }
            return servers;
        }
        return new ArrayList<>();
    }

    public List<JVultrDnsRecord> getDNSRecords(String domain) throws JVultrException{
        JsonElement response = new JsonParser().parse(JVultrAPI.get(JVultrAPI.ENDPOINT + "v1/dns/records?domain=" + domain,apiKey));
        if(response.isJsonArray()){
            List<JVultrDnsRecord> records = new ArrayList<>();
            for(JsonElement element : response.getAsJsonArray()){
                if(element.isJsonObject())records.add(new JVultrDnsRecord((JsonObject) element));
            }
            return records;
        }
        return new ArrayList<>();
    }

    public List<JVultrDnsRecord> getDNSRecords(JVultrDns dns) throws JVultrException{
        return getDNSRecords(dns.getDomain());
    }

    public JVultrDns createDns(String domain ,String ip) throws JVultrException{
        HashMap<String , Object> params = new HashMap<>();
        params.put("domain" , domain);
        params.put("serverip" , ip);
        JVultrAPI.post(JVultrAPI.ENDPOINT + "v1/dns/create_domain",apiKey , params);
        return new JVultrDns(domain , new Date());
    }

    public void deleteDns(String domain) throws JVultrException{
        HashMap<String , Object> params = new HashMap<>();
        params.put("domain" , domain);
        JVultrAPI.post(JVultrAPI.ENDPOINT + "v1/dns/delete_domain", apiKey , params);
    }

    public void createRecord(String domain ,String subdomain , JVultrDnsRecord.Type type,
                                        String data , @Optional Integer ttl ,
                             @Optional Integer priority) throws JVultrException{
        HashMap<String , Object> params = new HashMap<>();
        params.put("domain" , domain);
        params.put("name" , subdomain);
        params.put("type" , type.toString());
        params.put("data" , data);
        if(ttl != null)params.put("ttl" , ttl);
        if(priority != null)params.put("priority" , priority);
        JVultrAPI.post(JVultrAPI.ENDPOINT + "v1/dns/create_record",apiKey , params);
    }

    public void deleteRecord(String domain , int id) throws JVultrException{
        HashMap<String , Object> params = new HashMap<>();
        params.put("domain" , domain);
        params.put("RECORDID" , id);
        JVultrAPI.post(JVultrAPI.ENDPOINT + "v1/dns/delete_record",apiKey , params);
    }

    public void updateRecord(String domain , int id) throws JVultrException{
        HashMap<String , Object> params = new HashMap<>();
        params.put("domain" , domain);
        params.put("RECORDID" , id);
        JVultrAPI.post(JVultrAPI.ENDPOINT + "v1/dns/update_record",apiKey , params);
    }

    public void destroySnapshot(String id) throws JVultrException{
        HashMap<String , Object> params = new HashMap<>();
        params.put("SNAPSHOTID" , id);
        JVultrAPI.post(JVultrAPI.ENDPOINT + "v1/snapshot/destroy",apiKey , params);
    }

    public void createSnapshot(int id) throws JVultrException{
        HashMap<String , Object> params = new HashMap<>();
        params.put("SUBID" , id);
        System.out.println(JVultrAPI.post(JVultrAPI.ENDPOINT + "v1/snapshot/create",apiKey, params));
    }

    public void destroySnapshot(JVultrSnapshot snapshot) throws JVultrException{
        destroySnapshot(snapshot.getId());
    }

    public void createSnapshot(JVultrServer server) throws JVultrException{
        createSnapshot(server.getId());
    }

    /**
     * Create a new Vultr Server
     * <p><a href="https://www.vultr.com/api/#server_create" target="_blank">Vultr API Doc</a></p>
     * @param regionId Region id to create this virtual machine in.
     * @param planId Plan id to use when creating this virtual machine.
     * @param osId Operating systems'id to use.
     * @param ipxeChainUrl If you've selected the 'custom' operating system, this can be set to chainload the specified URL on bootup, via iPXE.
     * @param isoId If you've selected the 'custom' operating system, this is the ID of a specific ISO to mount during the deployment.
     * @param scriptId If you've not selected a 'custom' operating system, this can be the script id of a startup script to execute on boot.
     * @param snapshotId If you've selected the 'snapshot' operating system, this should be the snapshot id.
     * @param enableIpv6 If true, an IPv6 subnet will be assigned to the machine (where available).
     * @param enablePrivateNetwork If true, private networking support will be added to the new server.
     * @param label This is a text label that will be shown in the control panel.
     * @param sshKeyIds List of SSH keys to apply to this server on install (only valid for Linux/FreeBSD).
     * @param autoBackups If yes, automatic backups will be enabled for this server (these have an extra charge associated with them).
     * @param appId If you've selected the 'application' operating system, this is the Application id to launch.
     * @param userData Base64 encoded cloud-init user-data
     * @param notifyActivate If true, an activation email will be sent when the server is ready.
     * @param ddosProtection If true, DDOS protection will be enabled on the subscription (there is an additional charge for this).
     * @return An instance of the JVultrServer created
     * @throws JVultrException if an Error occured
     * @see JVultrServer
     */
    public JVultrServer createServer(int regionId , int planId , int osId ,
                             @Optional String ipxeChainUrl , @Optional Integer isoId ,
                                     @Optional Integer scriptId , @Optional String snapshotId ,
                                     @Optional Boolean enableIpv6 , @Optional Boolean enablePrivateNetwork ,
                                     @Optional String label , @Optional Integer sshKeyIds ,
                                     @Optional Boolean autoBackups, @Optional Integer appId ,
                                     @Optional String userData , @Optional Boolean notifyActivate ,
                                     @Optional Boolean ddosProtection, @Optional int subID,
                                     @Optional String host) throws JVultrException{
        HashMap<String , Object> params = new HashMap<>();
        params.put("DCID" , regionId);
        params.put("VPSPLANID" , planId);
        params.put("OSID" , osId);
        if(ipxeChainUrl != null)params.put("ipxe_chain_url" , ipxeChainUrl);
        if(isoId != null)params.put("ISOID",isoId);
        if(scriptId != null)params.put("SCRIPTID",scriptId);
        if(snapshotId != null)params.put("SNAPSHOTID",snapshotId);
        if(enableIpv6 != null)params.put("enable_ipv6",enableIpv6 ? "yes" : "no");
        if(enablePrivateNetwork != null)params.put("enable_private_network",enablePrivateNetwork ? "yes" : "no");
        if(label != null)params.put("label",label);
        if(sshKeyIds != null)params.put("SSHKEYID",sshKeyIds);
        if(autoBackups != null)params.put("auto_backups" ,autoBackups? "yes" : "no");
        if(appId != null)params.put("APPID",appId);
        if(userData!= null)params.put("userdata",userData);
        if(notifyActivate != null)params.put("notify_activate",notifyActivate ? "yes" : "no");
        if(ddosProtection != null)params.put("ddos_protection",ddosProtection? "yes" : "no");
        if (subID != -1) params.put("floating_v4_SUBID", subID);
		if (host != null) params.put("hostname", host);
        JsonElement response = new JsonParser().parse(JVultrAPI.post(JVultrAPI.ENDPOINT + "v1/server/create", apiKey, params));
        if(response.isJsonObject()){
            return getSevers().get(((JsonObject)response).get("SUBID").getAsInt());
        }else return null;
    }

    public JVultrServer createServer(JVultrRegion region, JVultrPlan plan , JVultrOS os ,
                                     @Optional String ipxeChainUrl , @Optional JVultrISO iso ,
                                     @Optional JVultrScript script , @Optional JVultrSnapshot snapshot ,
                                     @Optional Boolean enableIpv6 ,@Optional Boolean enablePrivateNetwork ,
                                     @Optional String label , @Optional Integer sshKey ,
                                     @Optional Boolean autoBackups , @Optional JVultrApplication app ,
                                     @Optional String userData , @Optional Boolean notifyActivate ,
                                     @Optional Boolean ddosProtection, @Optional int floating_SUBID,
                                     @Optional String host) throws JVultrException{
        return createServer(region.getId() , plan.getId() , os.getId() , ipxeChainUrl , iso != null ? iso.getId() : null
        , script != null ? script.getId() : null , snapshot != null ? snapshot.getId() : null , enableIpv6 , enablePrivateNetwork , label , sshKey != null ? sshKey : null,
                autoBackups , app != null ? app.getId() : null , userData , notifyActivate , ddosProtection,
				floating_SUBID, host);
    }
    public JVultrServer createServer(int regionId , int planId , int osId) throws JVultrException{
        return createServer(regionId, planId, osId , null , null , null , null , null , null
                , null , null , null , null , null , null ,null, -1, null);
    }

    public JVultrServer createServer(JVultrRegion region, JVultrPlan plan , JVultrOS os) throws JVultrException{
        return createServer(region.getId(), plan.getId(), os.getId());
    }

    public void destroyServer(int id) throws JVultrException {
        HashMap<String , Object> params = new HashMap<>();
        params.put("SUBID" , id);
        JVultrAPI.post(JVultrAPI.ENDPOINT + "v1/server/destroy",apiKey, params);
    }

    public void destroyServer(JVultrServer server) throws JVultrException{
        destroyServer(server.getId());
    }

}
