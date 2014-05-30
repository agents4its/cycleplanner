/*
Copyright 2013 Marcel Német

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package cvut.fel.nemetma1.routingService;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Class for initialising a RoutingService
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class RoutingServiceStarter extends HttpServlet {

    /**
     * initialises a RoutingService
     * @throws ServletException 
     */
    @Override
    public void init() throws ServletException {
        System.out.println("************");
        System.out.println("*** Initializing Routing Service ***");
        RoutingService r=RoutingService.INSTANCE;
        System.out.println("***********");

    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }
}
