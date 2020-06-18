### Leads processing 

Here is a basic example of how to handle leads using webhooks. 
The main idea is to use webhooks as a signal for the sync, not loaded leads. 

Please ensure that your webhook endpoint is fast enough. Do not load all leads in the webhook handler. The LID webhooks sender uses a 20-second timeout.
It is better to make the full sync of leads a separate job. Or do an initial import with a console program.



```java
package com.example.lid;

import com.ivelum.Cub;
import com.ivelum.net.Params;
import com.ivelum.model.CubObject;
import com.ivelum.model.Lead;
import com.ivelum.model.Organization;
import com.ivelum.model.State;
import com.ivelum.exception.CubException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class HelloController {
	public HelloController() {
		Cub.apiKey = "application secret key";
	}

	@PostMapping("/")
	public String index(@RequestBody Map<String, Object> payload) {
		// The better approach uses webhooks as a signal to sync your data
		// There can be a race condition between the most recent webhooks and webhooks with outdated data
		// (for example, on retries after failed requests);
		// webhooks can get lost, and even the most reliable systems can fail;
		// webhooks can be sent unintentionally from stage environments (because of misconfigurations)
		// So, webhooks should be considered as signals to pull the latest data through API.
		// Using this most recent data pulled through the API, you can update or create your local data model.
		Date fromDate = lastModifiedLeadTime(); // last webhook date

		int offset = 0;  // initial offset
		int count = 50;  // number of leads per "page"

		Params params = new Params();
		// ask API to "expand" nested objects see https://github.com/praetoriandigital/cub-java#expandable-objects
		params.setExpands("organization", "organization__state", "organization__state__country");
		params.setCount(count);

		List<CubObject> result;
		do {
			try {
				result = Lead.list(fromDate, params);  // getting leads since last lead received using paging
				for (CubObject l : result) {
					//https://github.com/praetoriandigital/cub-java/blob/master/src/main/java/com/ivelum/model/Lead.java
					Lead lead = (Lead) l;
					// lead.data contains submitted form details
					if (lead.organization != null ) {  // has nested object
						// https://github.com/praetoriandigital/cub-java/blob/master/src/main/java/com/ivelum/model/Organization.java
						Organization org = lead.organization.getExpanded();
						// https://github.com/praetoriandigital/cub-java/blob/master/src/main/java/com/ivelum/model/State.java
						if (org.state != null ) {
							State state = org.state.getExpanded();
						}
					}
					// all information about lead loaded .... can save it
				}
			} catch (CubException e) {
				// ... handle exception. Base cub-java exception 
			}
			params.setOffset(offset += count); // increase offset by page size
		} while (result.size() > 0); // stop when zero items returned
        // This sample loads all missing leads. But you loading just one page will be 
        // enough to keep your leads in sync after the initial import.  
		return "";
	}

	/**
	 * Stub, returns date of the latest lead
	 */
	protected Date lastModifiedLeadTime() {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, -2);  // 2 days ago
		return cal.getTime();
	}

}
```
