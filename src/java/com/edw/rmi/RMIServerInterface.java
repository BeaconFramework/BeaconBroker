/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edw.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author agalletta
 */
public interface RMIServerInterface extends Remote{
    
     public boolean stackInstantiate(String template,String templateId,String endpoint,String user,String tenant,String password,String region,String idCloud) throws RemoteException;
    
     
     public ArrayList getListResource(String endpoint,String user,String tenant,String password,String templateId) throws RemoteException;
   
}
