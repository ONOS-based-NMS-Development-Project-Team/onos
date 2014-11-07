/*
 * Copyright 2014 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onlab.onos.sdnip.cli;

import java.util.Collection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.Option;
import org.onlab.onos.cli.AbstractShellCommand;
import org.onlab.onos.sdnip.RouteEntry;
import org.onlab.onos.sdnip.SdnIpService;

/**
 * Command to show the list of routes in SDN-IP's routing table.
 */
@Command(scope = "onos", name = "routes",
        description = "Lists all routes known to SDN-IP")
public class RoutesListCommand extends AbstractShellCommand {
    @Option(name = "-s", aliases = "--summary",
            description = "SDN-IP routes summary",
            required = false, multiValued = false)
    private boolean routesSummary = false;

    private static final String FORMAT_SUMMARY = "Total SDN-IP routes = %d";
    private static final String FORMAT_ROUTE =
            "prefix=%s, nexthop=%s";

    @Override
    protected void execute() {
        SdnIpService service = get(SdnIpService.class);

        // Print summary of the routes
        if (routesSummary) {
            printSummary(service.getRoutes());
            return;
        }

        // Print all routes
        printRoutes(service.getRoutes());
    }

    /**
     * Prints summary of the routes.
     *
     * @param routes the routes
     */
    private void printSummary(Collection<RouteEntry> routes) {
        if (outputJson()) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode result = mapper.createObjectNode();
            result.put("totalRoutes", routes.size());
            print("%s", result);
        } else {
            print(FORMAT_SUMMARY, routes.size());
        }
    }

    /**
     * Prints all routes.
     *
     * @param routes the routes to print
     */
    private void printRoutes(Collection<RouteEntry> routes) {
        if (outputJson()) {
            print("%s", json(routes));
        } else {
            for (RouteEntry route : routes) {
                printRoute(route);
            }
        }
    }

    /**
     * Prints a route.
     *
     * @param route the route to print
     */
    private void printRoute(RouteEntry route) {
        if (route != null) {
            print(FORMAT_ROUTE, route.prefix(), route.nextHop());
        }
    }

    /**
     * Produces a JSON array of routes.
     *
     * @param routes the routes with the data
     * @return JSON array with the routes
     */
    private JsonNode json(Collection<RouteEntry> routes) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode result = mapper.createArrayNode();

        for (RouteEntry route : routes) {
            result.add(json(mapper, route));
        }
        return result;
    }

    /**
     * Produces JSON object for a route.
     *
     * @param mapper the JSON object mapper to use
     * @param route the route with the data
     * @return JSON object for the route
     */
    private ObjectNode json(ObjectMapper mapper, RouteEntry route) {
        ObjectNode result = mapper.createObjectNode();

        result.put("prefix", route.prefix().toString());
        result.put("nextHop", route.nextHop().toString());

        return result;
    }
}
