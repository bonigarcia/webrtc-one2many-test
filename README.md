Copyright © 2017 [Boni Garcia]. Licensed under [Apache 2.0 License].

webrtc-one2many-test
====================

This benchmark is aimed to assess the performance of WebRTC broadcasting application (one-to-many). To that aim, this repository is shipped with an **JUnit test**. This test uses the features provided by the Kurento Testing Framework (*kurento-test* Maven dependency). This test is configurable. The following table provides a summary of the configuration parameters:

| Parameter                   | Default Value                         | Description                                           |
|-----------------------------|---------------------------------------|-------------------------------------------------------|
| app.url                     | https://kurento.lab.fiware.org:8083/  | System Under Test (SUT) URL                           |
| num.viewers                 | 10                                    | Number of viewers                                     |
| viewers.rate                | 5                                     | Rate of new viewer incoming (in seconds)              |
| session.time                | 5                                     | Time (in seconds) in which all viewers are active     |
| output.folder               | .                                     | Folder in which the CSV result of the test is stored  |
| tab.timeout                 | 10                                    | Timeout (in seconds) to wait loading a new tab        |

The test can be executed from the command line. To do that, first of all this repository should be cloned from GitHub, and the it can be executed as follows:

```bash
mvn test -Dparam1=value1 -Dparam2=value ...
```

The first action that this test execute is to start the web application (implemented with spring-boot) that internally uses KurentoClient to control the KMS.

It is highly recommended to run this test in an Linux, since  the test uses native utilities. Therefore, this application must be installed in the machine running the test:

- Google Chrome (latest stable version). In Ubuntu, it can be done with the command line as follows:

```bash
sudo apt-get install libxss1 libappindicator1 libindicator7
wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
sudo dpkg -i google-chrome*.deb
sudo apt-get install -f
```
- [Tesseract-ocr]. Utility for OCR. This tool is mandatory to carry out the end-to-end (browser to browser) latency calculation. It can be installed in Ubuntu with the following command:

```bash
sudo apt-get install tesseract-ocr
sudo mkdir -p /bytedeco/javacpp-presets/tesseract/cppbuild/linux-x86_64/share/tessdata/
sudo ln -s /usr/share/tesseract-ocr/tessdata/eng.traineddata /bytedeco/javacpp-presets/tesseract/cppbuild/linux-x86_64/share/tessdata/eng.traineddata
```
- [qpsnr]. Utility to calculate video quality (SSIM/PSNR). This tool is mandatory to carry out the video quality evaluation (i.e., if the flag video.quality.ssim or video.quality.psnr are set to true). In order to install it in Ubuntu, the latest version of the deb package should be downloaded from the qpsnr web, and the install it with the Debian package manager:

When the test finishes, a CSV files will be created by the test. A CSV file is a comma separated values file, and it can be opened using a spreadsheet processor, for instance Microsoft Excel or LibreOffice Calc. It is important to be aware of the content of this file. The constraints are the following:

- Each column is a set of values of a given feature
- Each row is an instant sample of the corresponding feature (column)
- Each sample (i.e. each row) is taken each second

The format of the columns in this CSV file are the following:

- First column. Header name: E2ELatencyMs. This set of data is the end-to-end latency (i.e. browser to browser) in milliseconds
- Next columns. WebRTC stats. Each column name has the same pattern: \<local|remote\>\<Audio|Video\>\<statName\>. Therefore, we can read three different parts in each column:
	- \<local|remote\>. If the stat start with local that means that the metric has taken in presenter side. If the stat start with remote that means that the metric has taken in presenter side.
	- \<Audio|Video\>. This value distinguish metrics for Audio and Video.
	- \<statName\>. The final part of the column is the WebRTC metric name, e.g. googEchoCancellationReturnLoss, JitterBufferMs, packetsLost, among many others. The complete list of this WebRTC stats is the official standard. See the documentation (https://www.w3.org/TR/webrtc-stats/) for further information on each metric, and also the unit of each measurement.

Due to the fact the metrics are gathered using different mechanisms (i.e. client WebRTC stats, KMS internal latencies, end-to-end latency, video quality) it is very likely that the boundary values are not consistent. In other words, in order to process correctly the gathered data, the edge values (first and lasts rows) might be discarded.

Licensing and distribution
--------------------------

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


[Apache 2.0 License]: http://www.apache.org/licenses/LICENSE-2.0
[Tesseract-ocr]: https://github.com/tesseract-ocr
[Boni Garcia]: http://bonigarcia.github.io/
