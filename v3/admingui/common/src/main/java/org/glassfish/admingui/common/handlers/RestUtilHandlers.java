/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

/*
 * RestUitlHandlers.java
 *
 * Created on July 1,2010  9:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
/**
 *
 * @author anilam
 */
package org.glassfish.admingui.common.handlers;

import com.sun.jsftemplating.annotation.Handler;
import com.sun.jsftemplating.annotation.HandlerInput;
import com.sun.jsftemplating.annotation.HandlerOutput;

import com.sun.jsftemplating.layout.descriptors.handler.HandlerContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.glassfish.admingui.common.util.GuiUtil;

public class RestUtilHandlers {

    public RestUtilHandlers() {
    }

    @Handler(id = "gf.callRestAndExtractMsgProps",
        input = {
            @HandlerInput(name="endpoint", type=String.class, required=true),
            @HandlerInput(name="attrs", type=Map.class, required=false),
            @HandlerInput(name="method", type=String.class, defaultValue="post"),
            @HandlerInput(name="index", type=Integer.class, defaultValue="0")
        },
        output = {
            @HandlerOutput(name = "keyList", type = List.class),
            @HandlerOutput(name = "propsMap", type = Map.class),
            @HandlerOutput(name = "listEmpty", type = Boolean.class)
        })
    public static void callRestAndExtractResponse(HandlerContext handlerCtx) {
        Map<String, Object> attrs = (Map<String, Object>) handlerCtx.getInputValue("attrs");
        String endpoint = (String) handlerCtx.getInputValue("endpoint");
        String method = ((String) handlerCtx.getInputValue("method")).toLowerCase();
        int index = (Integer) handlerCtx.getInputValue("index");
        try{
            Map responseMap = RestApiHandlers.restRequest( endpoint , attrs, method , handlerCtx);
            ArrayList  messages = (ArrayList) responseMap.get("messages");
            Map message = (Map) messages.get(index);
            List<Map<String, String>> props = (List<Map<String, String>>) message.get("properties");
            processProps(props, handlerCtx);
        }catch (Exception ex){
            GuiUtil.getLogger().severe("Error in callRestAndExtratResponse ; \nendpoint = " + endpoint + "attrs=" + attrs + "method="+method);
            //we don't need to call GuiUtil.handleError() because thats taken care of in restRequest() when we pass in the handler.
        }
    }

    /*
     * This handler takes in the properties list of "message" which is from the RestResponse.
     * extrct the info from this list.
     */
    @Handler(id = "gf.getMessageProps",
        input = {
            @HandlerInput(name = "messageListProps", type = List.class, required = true)
        },
        output = {
            @HandlerOutput(name = "keyList", type = List.class),
            @HandlerOutput(name = "propsMap", type = Map.class),
            @HandlerOutput(name = "listEmpty", type = Boolean.class)
        })
    public static void getMessageProps(HandlerContext handlerCtx) {
        //If restRequest() change to output json,  this needs to be changed.
        List<Map<String, String>> props = (List<Map<String, String>>) handlerCtx.getInputValue("messageListProps");
        processProps(props, handlerCtx);
    }


    public static void processProps(List<Map<String, String>>props, HandlerContext handlerCtx){
        List keyList = new ArrayList();
        Map propsMap = new HashMap();
        try{
            for(Map<String, String> oneProp : props){
                keyList.add(oneProp.get("name"));
                propsMap.put(oneProp.get("name"), oneProp.get("value"));
            }
         }catch(Exception ex){
             //log error ?
         }
        handlerCtx.setOutputValue("keyList",  keyList);
        handlerCtx.setOutputValue("propsMap",  propsMap);
        handlerCtx.setOutputValue("listEmpty",  keyList.isEmpty());
     }
}
        
 
