/**
 * CONFIGURATION
 */
const CONFIG = {
  timezone: Session.getScriptTimeZone() || "Asia/Riyadh",
  dateFormat: "dd-MM-yy",
  headers: ["Reported At", "Description", "Image URLs"]
};

/**
 * Handle GET requests.
 * Returns a list of all bug reports from all daily tabs.
 */
function doGet() {
  try {
    const spreadsheet = SpreadsheetApp.getActiveSpreadsheet();
    const sheets = spreadsheet.getSheets();
    const allBugs = [];

    sheets.forEach(sheet => {
      const data = sheet.getDataRange().getValues();
      if (data.length <= 1) return; // Only headers

      for (let i = 1; i < data.length; i++) {
        const row = data[i];
        const description = String(row[1] || "").trim();
        if (!description) continue;

        const rawImages = String(row[2] || "");
        const images = rawImages ? rawImages.split("\n").map(s => s.trim()).filter(s => s.length > 0) : [];

        allBugs.push({
          reportedAtIso: String(row[0] || ""),
          description: description,
          imageUris: images
        });
      }
    });

    allBugs.sort((a, b) => new Date(b.reportedAtIso) - new Date(a.reportedAtIso));

    return jsonResponse_(200, { ok: true, bugs: allBugs });
  } catch (error) {
    return jsonResponse_(500, { ok: false, message: error.toString() });
  }
}

/**
 * Handle POST requests from Android App.
 */
function doPost(e) {
  const lock = LockService.getDocumentLock();
  try {
    lock.tryLock(20000);
    const payload = JSON.parse(e.postData.contents);
    const spreadsheet = SpreadsheetApp.getActiveSpreadsheet();
    // Get or create today's sheet based on the current date and the specified date format in the CONFIG constant.
    const tabName = Utilities.formatDate(new Date(), CONFIG.timezone, CONFIG.dateFormat);
    const sheet = getOrCreateSheet_(spreadsheet, tabName);
    //append the new bug report to the sheet
    const imageUrlsString = (payload.imageUris || []).join("\n");

    sheet.appendRow([
      payload.reportedAtIso || new Date().toISOString(),
      payload.description,
      imageUrlsString
    ]);

    return jsonResponse_(200, { ok: true, message: "Success" });
  } catch (error) {
    return jsonResponse_(500, { ok: false, message: error.toString() });
  } finally {
    if (lock.hasLock()) lock.releaseLock();
  }
}

function getOrCreateSheet_(spreadsheet, tabName) {
  let sheet = spreadsheet.getSheetByName(tabName);
  if (sheet) return sheet;
  sheet = spreadsheet.insertSheet(tabName);
  //Sheet Header
  sheet.getRange(1, 1, 1, CONFIG.headers.length).setValues([CONFIG.headers]).setFontWeight("bold");
  sheet.setFrozenRows(1);
  return sheet;
}

function jsonResponse_(statusCode, payload) {
  return ContentService.createTextOutput(JSON.stringify(payload))
    .setMimeType(ContentService.MimeType.JSON);
}
