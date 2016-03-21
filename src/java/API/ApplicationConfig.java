/**APPENDIX: How to apply the Apache License to your work.
*
*      To apply the Apache License to your work, attach the following
*      boilerplate notice, with the fields enclosed by brackets "{}"
*      replaced with your own identifying information. (Don't include
*      the brackets!)  The text should be enclosed in the appropriate
*      comment syntax for the file format. We also recommend that a
*      file or class name and description of purpose be included on the
*      same "printed page" as the copyright notice for easier
*      identification within third-party archives.
*
*   Copyright 2016, University of Messina
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/

package API;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author Giuseppe Tricomi
 */
@javax.ws.rs.ApplicationPath("fednet")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        //resources.add(JClouds_Adapter.Tenant.class);
        resources.add(API.EASTAPI.Link.class);
        //resources.add(NTHAPI.NetworkResource.class);
        resources.add(API.EASTAPI.LinksResource.class);
        //resources.add(NTHAPI.SiteResource.class);
        resources.add(API.EASTAPI.NetworkSegmentResource.class);
        resources.add(API.EASTAPI.NetworksegmentResource.class);
        resources.add(API.EASTAPI.UserResource.class);
        resources.add(API.EASTAPI.UsersResource.class);
        resources.add(API.NTHAPI.Manifestcreate.class);
        resources.add(API.NTHAPI.ManifestcreatesResource.class);
        resources.add(API.NTHAPI.NetworkResource.class);
        resources.add(API.NTHAPI.NetworksResource.class);
        resources.add(API.NTHAPI.SiteResource.class);
        resources.add(API.NTHAPI.SitesResource.class);
        resources.add(API.NTHAPI.Tenant.class);
        resources.add(API.NTHAPI.TenantResource.class);
        resources.add(API.NTHAPI.Users.class);
        resources.add(API.NTHAPI.UserssResource.class);
    }
    
}
