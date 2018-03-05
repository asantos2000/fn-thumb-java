package com.origoconsul.fn.thumb.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A helper class that represents a strongly typed S3 EventNotification item
 * sent to SQS, SNS, or Lambda.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class S3EventNotification {

	private final List<S3EventNotificationRecord> records;

	@JsonCreator
	public S3EventNotification(@JsonProperty(value = "Records") List<S3EventNotificationRecord> records) {
		this.records = records;
	}

	/**
	 * @return the records in this notification
	 */
	@JsonProperty(value = "Records")
	public List<S3EventNotificationRecord> getRecords() {
		return records;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class UserIdentityEntity {

		private final String principalId;

		@JsonCreator
		public UserIdentityEntity(@JsonProperty(value = "principalId") String principalId) {
			this.principalId = principalId;
		}

		public String getPrincipalId() {
			return principalId;
		}
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class S3BucketEntity {

		private final String name;
		private final UserIdentityEntity ownerIdentity;
		private final String arn;

		@JsonCreator
		public S3BucketEntity(@JsonProperty(value = "name") String name,
				@JsonProperty(value = "ownerIdentity") UserIdentityEntity ownerIdentity,
				@JsonProperty(value = "arn") String arn) {
			this.name = name;
			this.ownerIdentity = ownerIdentity;
			this.arn = arn;
		}

		public String getName() {
			return name;
		}

		public UserIdentityEntity getOwnerIdentity() {
			return ownerIdentity;
		}

		public String getArn() {
			return arn;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class S3ObjectEntity {

		private final String key;
		private final Long size;
		private final String eTag;
		private final String versionId;
		private final String sequencer;
		private final String contentType;

		@Deprecated
		public S3ObjectEntity(String key, Integer size, String eTag, String versionId, String contentType) {
			this.key = key;
			this.size = size == null ? null : size.longValue();
			this.eTag = eTag;
			this.versionId = versionId;
			this.sequencer = null;
			this.contentType = null;
		}

		@Deprecated
		public S3ObjectEntity(String key, Long size, String eTag, String versionId) {
			this(key, size, eTag, versionId, null, null);
		}

		@JsonCreator
		public S3ObjectEntity(@JsonProperty(value = "key") String key, @JsonProperty(value = "size") Long size,
				@JsonProperty(value = "eTag") String eTag, @JsonProperty(value = "versionId") String versionId,
				@JsonProperty(value = "sequencer") String sequencer, @JsonProperty("contentType") String contentType) {
			this.key = key;
			this.size = size;
			this.eTag = eTag;
			this.versionId = versionId;
			this.sequencer = sequencer;
			this.contentType = contentType;
		}

		public String getKey() {
			return key;
		}

		/**
		 * @deprecated use {@link #getSizeAsLong()} instead.
		 */
		@Deprecated
		@JsonIgnore
		public Integer getSize() {
			return size == null ? null : size.intValue();
		}

		@JsonProperty(value = "size")
		public Long getSizeAsLong() {
			return size;
		}

		public String geteTag() {
			return eTag;
		}

		public String getVersionId() {
			return versionId;
		}

		public String getSequencer() {
			return sequencer;
		}

		public String getContentType() {
			return contentType;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class S3Entity {

		private final String configurationId;
		private final S3BucketEntity bucket;
		private final S3ObjectEntity object;
		private final String s3SchemaVersion;

		@JsonCreator
		public S3Entity(@JsonProperty(value = "configurationId") String configurationId,
				@JsonProperty(value = "bucket") S3BucketEntity bucket,
				@JsonProperty(value = "object") S3ObjectEntity object,
				@JsonProperty(value = "s3SchemaVersion") String s3SchemaVersion) {
			this.configurationId = configurationId;
			this.bucket = bucket;
			this.object = object;
			this.s3SchemaVersion = s3SchemaVersion;
		}

		public String getConfigurationId() {
			return configurationId;
		}

		public S3BucketEntity getBucket() {
			return bucket;
		}

		public S3ObjectEntity getObject() {
			return object;
		}

		public String getS3SchemaVersion() {
			return s3SchemaVersion;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class RequestParametersEntity {

		private final String sourceIPAddress;

		@JsonCreator
		public RequestParametersEntity(@JsonProperty(value = "sourceIPAddress") String sourceIPAddress) {
			this.sourceIPAddress = sourceIPAddress;
		}

		public String getSourceIPAddress() {
			return sourceIPAddress;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ResponseElementsEntity {

		private final String xAmzId2;
		private final String xAmzRequestId;

		@JsonCreator
		public ResponseElementsEntity(@JsonProperty(value = "x-amz-id-2") String xAmzId2,
				@JsonProperty(value = "x-amz-request-id") String xAmzRequestId) {
			this.xAmzId2 = xAmzId2;
			this.xAmzRequestId = xAmzRequestId;
		}

		@JsonProperty("x-amz-id-2")
		public String getxAmzId2() {
			return xAmzId2;
		}

		@JsonProperty("x-amz-request-id")
		public String getxAmzRequestId() {
			return xAmzRequestId;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class S3EventNotificationRecord {

		private final String awsRegion;
		private final String eventName;
		private final String eventSource;
		private final String eventTime;
		private final String eventVersion;
		private final RequestParametersEntity requestParameters;
		private final ResponseElementsEntity responseElements;
		private final S3Entity s3;
		private final UserIdentityEntity userIdentity;

		@JsonCreator
		public S3EventNotificationRecord(@JsonProperty(value = "awsRegion") String awsRegion,
				@JsonProperty(value = "eventName") String eventName,
				@JsonProperty(value = "eventSource") String eventSource,
				@JsonProperty(value = "eventTime") String eventTime,
				@JsonProperty(value = "eventVersion") String eventVersion,
				@JsonProperty(value = "requestParameters") RequestParametersEntity requestParameters,
				@JsonProperty(value = "responseElements") ResponseElementsEntity responseElements,
				@JsonProperty(value = "s3") S3Entity s3,
				@JsonProperty(value = "userIdentity") UserIdentityEntity userIdentity) {
			this.awsRegion = awsRegion;
			this.eventName = eventName;
			this.eventSource = eventSource;
			this.eventTime = eventTime;

			this.eventVersion = eventVersion;
			this.requestParameters = requestParameters;
			this.responseElements = responseElements;
			this.s3 = s3;
			this.userIdentity = userIdentity;
		}

		public String getAwsRegion() {
			return awsRegion;
		}

		public String getEventName() {
			return eventName;
		}

		public String getEventSource() {
			return eventSource;
		}

		public String getEventTime() {
			return eventTime;
		}

		public String getEventVersion() {
			return eventVersion;
		}

		public RequestParametersEntity getRequestParameters() {
			return requestParameters;
		}

		public ResponseElementsEntity getResponseElements() {
			return responseElements;
		}

		public S3Entity getS3() {
			return s3;
		}

		public UserIdentityEntity getUserIdentity() {
			return userIdentity;
		}
	}
}
