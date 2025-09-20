# Notification Service

A lightweight, high-performance email notification service built with Java and Netty. This service provides a RESTful
API for sending emails with support for HTML/text content, attachments, and multiple recipient types.

## Features

- 🚀 **High Performance**: Built on Netty for asynchronous, non-blocking I/O
- 📧 **Email Delivery**: Send emails with HTML and text content
- 📎 **Attachment Support**: Support for multiple file attachments
- 👥 **Multiple Recipients**: Send to To, CC, and BCC recipients
- 💾 **Email Tracking**: PostgreSQL database for tracking email delivery status
- 🔒 **Security**: CORS handling and security headers
- 📊 **Monitoring**: Comprehensive logging and request tracking
- 🐳 **Container Ready**: Environment-based configuration for easy deployment

## Technology Stack

- **Java**
- **Netty** - HTTP server and networking
- **JavaMail** - Email delivery
- **PostgreSQL** - Email record storage
- **Jackson** - JSON processing
- **SLF4J** - Logging

## API Documentation

### Send Email

**Endpoint:** `POST /api/v1/email/send`

**Request Body:**

```json
{
  "subject": "Email Subject",
  "htmlBody": "<h1>HTML Content</h1>",
  "textBody": "Plain text content",
  "recipients": {
    "to": [
      "recipient1@example.com",
      "recipient2@example.com"
    ],
    "cc": [
      "cc@example.com"
    ],
    "bcc": [
      "bcc@example.com"
    ]
  },
  "attachments": [
    {
      "filename": "document.pdf",
      "content": "base64-encoded-content",
      "mimeType": "application/pdf"
    }
  ]
}
```

**Response:**

```json
{
  "requestId": "uuid-string"
}
```

### Get Email Status

**Endpoint:** `GET /api/v1/email/find?requestId={uuid_str}`

**Response Body:**

```json
{
  "requestId": "uuid_str",
  "subject": "Email Subject",
  "hasHtmlBody": true,
  "hasTextBody": true,
  "hasAttachments": false,
  "emailFrom": "someone@email.com",
  "emailTo": "to1@email.com, to2@email.com",
  "emailCc": "cc1@email.com, cc2@email.com",
  "emailBcc": "bcc1@email.com, bcc2@email.com",
  "receivedAt": "2020-02-20T10:30:00",
  "sentAt": "2020-02-20T10:30:05",
  "errorMessage": null
}
```
