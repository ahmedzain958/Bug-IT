const CONFIG = {
  timezone: Session.getScriptTimeZone() || "Asia/Riyadh",
  dateFormat: "dd-MM-yy",
  headers: ["Reported At", "Description", "Image URIs", "Device", "App Version"],
  maxDescriptionLength: 2000,
  maxImages: 10
};

function doGet() {
  return jsonResponse_(200, {
    ok: true,
    service: "bug-upload",
    timezone: CONFIG.timezone,
    nowTab: Utilities.formatDate(new Date(), CONFIG.timezone, CONFIG.dateFormat)
  });
}

function doPost(e) {
  const lock = LockService.getDocumentLock();

  try {
    lock.tryLock(20000);

    const payload = parsePayload_(e);
    validatePayload_(payload);

    const spreadsheet = SpreadsheetApp.getActiveSpreadsheet();
    const tabName = Utilities.formatDate(new Date(), CONFIG.timezone, CONFIG.dateFormat);
    const sheet = getOrCreateSheet_(spreadsheet, tabName);

    // Join image URLs with newline for readability
    const imageUrisString = (payload.imageUris || []).join("\n");

    sheet.appendRow([
      payload.reportedAtIso || new Date().toISOString(),
      payload.description,
      imageUrisString,
      payload.deviceModel || "",
      payload.appVersion || ""
    ]);

    return jsonResponse_(200, {
      ok: true,
      tab: tabName,
      message: "Bug report uploaded successfully",
      rowsAffected: 1
    });
  } catch (error) {
    const statusCode = (error && error.code) ? error.code : 500;
    const message = error && error.message ? String(error.message) : "Unexpected server error";

    Logger.log("Error in doPost: " + message);
    Logger.log("Stack: " + error.stack);

    return jsonResponse_(statusCode, {
      ok: false,
      message: message,
      error: error ? error.toString() : "Unknown error"
    });
  } finally {
    if (lock.hasLock()) {
      lock.releaseLock();
    }
  }
}

function parsePayload_(e) {
  const raw = e && e.postData && e.postData.contents ? e.postData.contents : "";
  if (!raw) {
    throw { code: 400, message: "Request body is empty" };
  }

  try {
    return JSON.parse(raw);
  } catch (parseError) {
    throw { code: 400, message: "Invalid JSON payload: " + parseError.toString() };
  }
}

function validatePayload_(payload) {
  if (!payload || typeof payload !== "object") {
    throw { code: 400, message: "Payload must be a JSON object" };
  }

  if (typeof payload.description !== "string" || !payload.description.trim()) {
    throw { code: 400, message: "description is required and must be non-empty string" };
  }

  if (payload.description.length > CONFIG.maxDescriptionLength) {
    throw { code: 400, message: "description exceeds max length of " + CONFIG.maxDescriptionLength };
  }

  if (payload.imageUris && !Array.isArray(payload.imageUris)) {
    throw { code: 400, message: "imageUris must be an array of URLs" };
  }

  if (Array.isArray(payload.imageUris) && payload.imageUris.length > CONFIG.maxImages) {
    throw { code: 400, message: "Too many images (" + payload.imageUris.length + "). Max: " + CONFIG.maxImages };
  }

  // Validate image URLs are strings
  if (Array.isArray(payload.imageUris)) {
    for (let i = 0; i < payload.imageUris.length; i++) {
      if (typeof payload.imageUris[i] !== "string") {
        throw { code: 400, message: "imageUris[" + i + "] must be a string URL" };
      }
    }
  }
}

function getOrCreateSheet_(spreadsheet, tabName) {
  let sheet = spreadsheet.getSheetByName(tabName);
  if (sheet) {
    return sheet;
  }

  // Create new sheet with tab name
  sheet = spreadsheet.insertSheet(tabName);

  // Add headers
  sheet.getRange(1, 1, 1, CONFIG.headers.length).setValues([CONFIG.headers]);

  // Freeze header row
  sheet.setFrozenRows(1);

  // Auto-resize columns for readability
  sheet.autoResizeColumns(1, CONFIG.headers.length);

  return sheet;
}

function jsonResponse_(statusCode, payload) {
  const envelope = Object.assign({ statusCode: statusCode }, payload);
  return ContentService
    .createTextOutput(JSON.stringify(envelope))
    .setMimeType(ContentService.MimeType.JSON);
}

/**
 * Test function - call from Apps Script editor's Run menu
 * This validates the doPost function works correctly
 */
function testDoPost() {
  const testPayload = {
    description: "Test bug report",
    imageUris: ["https://i.ibb.co/test1.jpg", "https://i.ibb.co/test2.jpg"],
    reportedAtIso: new Date().toISOString(),
    deviceModel: "Test Device",
    appVersion: "1.0"
  };

  const mockEvent = {
    postData: {
      contents: JSON.stringify(testPayload)
    }
  };

  const response = doPost(mockEvent);
  Logger.log("Test response: " + response.getContent());
}
