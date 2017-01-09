-------------------- Grapes config.yml File -----------------------------------

# To run Grapes server, a YAML configuration file is needed with following information

# Grapes server
http:
  bindHost: localhost
  port: 8081
  adminPort: 8082
  adminUsername: admin
  adminPassword: admin
database:
  host: localhost
  port: 27017
  user:
  pwd: 
  datastore: grapes2
  dbsystem: mongodb
logging:
  level: INFO
  file:
    enabled: true
    threshold: ALL
    currentLogFilename: target/logs/grapes.out
    archivedLogFilenamePattern: target/archive/grapes_archive-%d.log.gz
    archivedFileCount: 5
    timeZone: UTC
community:
  issueTracker: https://github.com/Axway/Grapes/issues
  onlineHelp: https://github.com/Axway/Grapes/wiki
  
# file containing important messages
messageFile: C:\Users\user\Desktop\error.txt

# user intended to receive notification from Grapes
artifactNotificationRecipients: [user@axway.com]

mailing:
   host: smtp_host(mail.ptx.axway.int)
   port: smtp_port(587)
   user: smtp_user
   pwd: paswword
   sslTrust: mail.ptx.axway.int
   smtpFrom: smtp_user
   debug: false






-------------------- Grapes Error Message(error.txt) File -----------------------------------

# It contains error messages

VALIDATION_TYPE_NOT_SUPPORTED = This artifact does not need to be validated by Grapes. Supported types are %s
QUERYING_NON_PUBLISHED_ARTIFACTS_ERROR_STAGE_UPLOAD = You are uploading a non-published artifact. If you intend to transition this artifact to the General Available state, please submit a ticket to ECD Support Team specifying details related to the build job creating this artifact. Also include the information related to File name(Actual file Name) and Checksum value(Actual checksum). To submit a ticket use this link: https://techweb.axway.com/jira
QUERYING_NON_PUBLISHED_ARTIFACTS_ERROR_STAGE_PUBLISH = You are publishing an artifact that has not been promoted through ECD. Please submit a ticket to ECD Support Team specifying details related to the build job that created this artifact. Also include the information related to File name(Actual file Name) and Checksum value(Actual checksum). To submit a ticket use this link: https://techweb.axway.com/jira
ARTIFACT_NOT_PROMOTED_ERROR_MESSAGE = Artifact is not promoted

#Notification email configuration#
ARTIFACT_NOTIFICATION_EMAIL_SUBJECT = Webliv publish attempt for %s - untraceable
ARTIFACT_NOT_KNOWN_NOTIFICATION_EMAIL_BODY = Hello,<br><br>User %s is trying to publish <b>%s</b>.<br> Checksum is <b>%s</b>.<br> The artifact is not known.%s<br><br>Regards,<br>RD DevOps
ARTIFACT_NOT_PROMOTED_NOTIFICATION_EMAIL_BODY = Hello,<br><br>User %s is trying to publish <b>%s</b>.<br> Checksum is <b>%s</b>.<br> The artifact is not promoted.%s<br><br>Regards,<br>RD DevOps





