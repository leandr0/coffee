/**
 * 
 */
package com.lrgoncalves.coffee.model.hateos;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrgoncalves.coffee.model.JsonModel;


/**
 * @author lrgoncalves
 *
 */
public abstract class HATEOS <T extends JsonModel> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4109021349898901989L;

	/**
	 * 
	 */
	@JsonProperty
	protected Set<Link> links;

	protected ObjectMapper mapper = new ObjectMapper();

	private static final Logger LOG = LoggerFactory.getLogger(HATEOS.class);

	/**
	 * 
	 * @param order
	 * @return
	 * @throws URISyntaxException
	 * @throws JsonProcessingException 
	 */
	public abstract String generateHATEOS(final T model, final URI uri) throws URISyntaxException, JsonProcessingException;


	protected Link generateSelfLink(final HATEOS<?> instance,final URI uri,final String key) throws URISyntaxException {
		LOG.info("Generating self link");
		Link self = new Link();

		self.setHeaders(generateHeader("Accept", "application/json"))
		.setHref(generateSelfURI(uri, key))
		.setMethod("GET")
		.setRel("self");

		return self;
		//instance.getLinks().add(self);
	}

	protected URI generateSelfURI(final URI uri, final String key) throws URISyntaxException {

		return new URI(uri+"order/"+key);
	}


	protected Map<String, String> generateHeader(final String key, final String value) {
		LOG.info("Generating Headers");
		Map<String, String> headers = new HashMap<String, String>();
		LOG.info("Adding Key : "+key+" and Value : "+value);
		headers.put(key, value);

		return headers;
	}


	public String toJson() throws JsonProcessingException {

		return mapper.writeValueAsString(this);
	}

	protected String toJson(final T model) throws JsonProcessingException {

		return ResponseUtils.compileResponse(model.toJson(), mapper.writeValueAsString(this));

	}

	/**
	 * @return the links
	 */
	protected Set<Link> getLinks() {
		return links;
	}

	/**
	 * Representation of link within HATEOS
	 * @author lrgoncalves
	 *
	 */
	@SuppressWarnings("unused")
	protected class Link {

		/**
		 * Link for an action
		 */
		private URI href;

		/**
		 * Action
		 */
		private String rel;

		/**
		 * HTTP method
		 */
		private String method;

		/**
		 * Information for client about header parameters
		 */
		private Map<String, String> headers;

		/**
		 * @return the href
		 */
		public URI getHref() {
			return href;
		}

		/**
		 * @param href the href to set
		 * Link for an action
		 */
		public Link setHref(URI href) {
			this.href = href;
			return this;
		}

		/**
		 * @return the rel
		 */
		public String getRel() {
			return rel;
		}

		/**
		 * {@literal Action }
		 * @param rel the rel to set
		 *  
		 */
		public Link setRel(String rel) {
			this.rel = rel;
			return this;
		}

		/**
		 * @return the method
		 */
		public String getMethod() {
			return method;
		}

		/**
		 * @param method the method to set
		 * HTTP method
		 */
		public Link setMethod(String method) {
			this.method = method;
			return this;
		}

		/**
		 * @return the headers
		 */
		public Map<String, String> getHeaders() {
			return headers;
		}

		/**
		 * @param headers the headers to set
		 * Information for client about header parameters
		 */
		public Link setHeaders(Map<String, String> headers) {
			this.headers = headers;
			return this;
		}
	}

}
