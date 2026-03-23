# Bug-IT 🐞

Bug-IT is a lightweight Android utility designed to streamline bug reporting. It captures bug descriptions and screenshots, uploads images to a 3rd-party service, and logs everything into a centralized Google Sheet.

## 📺 Full Feature Demo

Watch the complete workflow of Bug-IT, from receiving an external intent to daily sheet organization.

> **Visual Guide**: ![Full Feature Demo](docs/full_demo.gif)
> *Placeholder: [FULL_DEMO_GIF]*

---

## 🚀 Key Features

### 1. Multi-Source Image Attachment
- **Gallery & Screenshot**: Users can select images from their device's gallery or capture a screenshot directly within the app using the `PixelCopy` API for high-quality captures.
- **Multiple Images**: Support for attaching multiple screenshots to a single bug report.
- **Image Management**: Easily remove incorrectly attached images before submission.

> **Visual Guide**: ![Image Selection Demo](docs/image_selection.gif)
> *Placeholder: [IMAGE_SELECTION_GIF]*

### 2. Mandatory Description & Validation
- Ensures data integrity by requiring a mandatory description before submission.
- Real-time UI feedback for validation errors.

> **Visual Guide**: ![Validation Demo](docs/validation.gif)
> *Placeholder: [VALIDATION_GIF]*

### 3. 3rd-Party Image Hosting (ImgBB)
To keep the Google Sheet lightweight and accessible:
- **Upload**: Images are sent to [ImgBB](https://imgbb.com/) via their REST API.
- **Attachment**: The API returns direct permanent URLs (e.g., `https://i.ibb.co/...`).
- **Storage**: These URLs are stored as text links in the "Image URLs" column of the sheet.

> **Visual Guide**: ![ImgBB Upload](docs/imgbb_upload.png)
> *Placeholder: [IMGBB_UPLOAD_SCREENSHOT]*

### 4. Daily Google Sheet Tabs
Bug data is organized efficiently in Google Sheets:
- **Dynamic Tabs**: The system automatically creates or selects a tab based on the current date (e.g., `26-09-23`, `27-09-23`).
- **Data Persistence**: Logs "Reported At", "Description", and a list of "Image URLs".

> **Visual Guide**: ![Sheet Tabs](docs/sheet_tabs.png)
> *Placeholder: [SHEET_TABS_SCREENSHOT]*

### 5. External Intent Integration (Direct Landing)
Initiate a bug report immediately by sharing images from other apps (Gallery, Photos, etc.).
- **Direct Landing**: Sharing an image lands the user directly on the bug creation screen, bypassing the list.
- **Support**: Handles both `ACTION_SEND` (single) and `ACTION_SEND_MULTIPLE` intents.

> **Visual Guide**: ![Intent Sharing](docs/intent_sharing.gif)
> *Placeholder: [INTENT_SHARING_GIF]*

### 6. Bug Reports List & Details
- **Unified List**: An initial screen displays a scrollable list of all submitted bugs, fetched from the Google Sheet.
- **Detail View**: Click on any bug to see its full description, a gallery of attached images, and the exact reporting time.
- **Automatic Refresh**: The list automatically refreshes after a new submission to show the latest entry at the top.

> **Visual Guide**: ![Bug Details](docs/bug_details.gif)
> *Placeholder: [BUG_DETAILS_GIF]*

---

## 🛠️ Setup Instructions

### 1. ImgBB API Key
1. Get a free API key from [api.imgbb.com](https://api.imgbb.com/).
2. Add it to your `gradle.properties`:
   ```properties
   IMGBB_API_KEY=a933d22793ecd04f66e6ada2350f96ee
   ```

### 2. Google Apps Script Deployment
1. Create a new Google Sheet.
2. Go to **Extensions > Apps Script**.
3. Paste the code from `docs/apps-script/UploadITAppsScript.gs`.
4. Click **Deploy > New Deployment**.
5. Select **Web App**, set access to **Anyone**, and click **Deploy**.
6. Copy the Web App URL and add it to your `gradle.properties`:
   ```properties
   BUG_UPLOAD_ENDPOINT=https://script.google.com/macros/s/.../exec
   ```

---

## 🏗️ Architecture Note
The app follows modern Android practices:
- **Jetpack Compose** for a declarative UI.
- **Coil 3** for asynchronous image loading and network fetching.
- **Retrofit** for REST API communication.
- **State Hoisting** & **ViewModel** (AndroidViewModel) for robust state management.
