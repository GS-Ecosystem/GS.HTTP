# GS.HTTP
GS HTTP is a lightweight and fast utility designed for checking HTTP requests.
* [**GS.HTTP** on APKPure](https://apkpure.com/gs-http/com.flet.gshttp)
* [**GS.HTTP** on RuStore](https://www.rustore.ru/catalog/app/com.flet.gshttp)
* [**GS.HTTP** on RuMarket](https://ruplay.market)
* [**GS.HTTP** on GitHub -> Releases](https://github.com/proto-gs/GS.HTTP/releases/tag/v1.0.6)
# Information
GS HTTP is a lightweight and fast utility designed specifically for web developers, system administrators, and anyone working with APIs and network requests.
With GS HTTP, you can:<br><br>
• Instantly check the HTTP status of any website (200 OK, 404 Not Found, etc.).<br>
• View site server responses, which include: body, headers, and site cookies.<br>
• Open the verified website in a browser.<br>
This application includes all the necessary settings for your workflow:<br><br>
Network and connection:<br>
• Auto-redirect — a toggle switch that follows website redirects.<br>
• Request timeout — the ability to set the request duration up to 30 seconds.<br>
Security and SSL:<br>
• Verify SSL — a toggle switch for strict certificate verification.<br>
• Ignore SSL errors — a toggle switch for self-signed certificates.<br>
User-Agent — by default, a user-agent is used in HTTP/1.1, and although it is not required, it is available in the application settings.<br>
Personalization and input:<br>
Theme design — the ability to change the application theme to light, dark, or set it to match the system.<br>
Language — the application is translated and currently available in two languages: English and Russian.<br>
Clear history and input — allows you to clear the history and current input in the application's scanner menu.<br>
This application also features a history log:<br>
• It is saved in an isolated, secure folder on Android, which requires no storage permissions.<br>
• The history does not store a large number of requests yet; instead, it overwrites older ones.<br>
• It is unique because it is stored locally on the user's device—the application has no servers for storage, and data is not transferred to third parties.<br><br>
The scanner menu in the application supports all 9 popular methods:<br>
GET, POST, HEAD, PUT, PATCH, DELETE, CONNECT, TRACE, OPTIONS
## Clone repository | Building app | Working with the project
There are two full-time IDLE programs where you can easily open and work with a project:
* [AndroidStudio](https://developer.android.com/studio)(recommended)
* [Intelij IDEA](https://www.jetbrains.com/idea/)<br><br>
Once you have downloaded IDLE from the official website, you can clone the repository using the command<br> `$ git clone https://github.com/proto-gs/GS.HTTP`<br><br>
To make changes to the repository, use the `git add` command. This will save all changes and stage them.<br> Commit with `git commit -m "Your description of the changes"` to commit the changes locally.<br> Push to GitHub with `git push` to upload files to the server.<br>
Using the `git pull` command, you will pull in all updates from the GitHub repository, provided there are no conflicts in your local project.
For this you will also need git pre-installed.<br><br>
Once you have successfully cloned the repository, all you have to do is compile and run it using `./gradlew`<br>
To compile the Release version, use the command `./gradlew assembleRelease`<br> To compile the Debug version, use the command `./gradlew assembleDebug`<br>
To stop compilation, use the command `./gradlew --stop`<br> For help and all the options of `./gradlew` use the command `./gradlew --help`<br><br>

When choosing operating systems, it is recommended to choose Linux.
## License

`GS.HTTP` is licensed under the terms of the MIT License.

For more information, see [LICENSE](/LICENSE) file.

License of components and third-party dependencies it relies on might differ, check `LICENSE` file in the corresponding folder.
