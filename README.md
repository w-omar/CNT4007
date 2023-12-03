# P2P Group 8

The goal of this project is to share files between hosts using a peer to peer file sharing protocol.

### Achievements
i.e. What you were able to achieve and what you were not.
### Build Instructions
View our video demo [here](youtube.com) or follow these instructions:
1. Install apache maven on remote machine
```
wget https://downloads.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz
tar -zxvf apache-maven-3.9.5-bin.tar.gz
mv apache-maven-3.9.5 ~/maven
```
2. Add the following lines to your `.tcshrc` file and run `source ~/.tcshrc`
```
setenv M2_HOME ~/maven
set path = ($path $M2_HOME/bin)
```
2. Extract `CNT4007.tar.gz` on the remote machine with
```
tar -zxvf ./CNT4007.tar.gz
```
3. Build project
```
cd CNT4007
mvn clean package
```
4. Specify testing parameters in `CNT4007/Common.cfg` and `CNT4007/PeerInfo.cfg`
5. Place the file to be shared into the project root directory (i.e. `CNT4007/<filename>`)
6. Configure your local machine to start `peerProcess` via SSH
- Extract `CNT4007.tar.gz` on your local machine
- Open in IntelliJ
- Right click `pom.xml` and select "Add as Maven Project"
- Navigate: Run -> Edit Configuration -> Add New Configuration -> Application
- Enter `StartRemotePeers` into Main class
- Enter `<filename>` into Program Arguments
- Apply
- enable passwordless ssh from your local machine
```
ssh-copy-id -i ~/.ssh/<key> <username>@lin114-00.cise.ufl.edu
```
- enter your CISE username into `remoteLogin.cfg`
- ensure an exact copy of `PeerInfo.cfg` is present in the local machines project directory
8. Run StartRemotePeers

### Contributors
| Name              | Email                   | Contributions                                                                                                                                                                                                                                                                                             |
|-------------------|-------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| William Collado  | collado.womar@ufl.edu   | Configured automated running of peerProcess on remote machines.<br/>Implemented server side logic for responding to piece and request messages.<br/>Implemented scheduling of neighbor selection.<br/>Contributed to peer subdirectory logic including reading and writing to the file being shared.<br/> |
| Vincent Cardone   | vcardone@ufl.edu        | Responsible for all of the logging file writing and functionality. Called the logging function in the appropriate places throughout the Peer, and Server files.<br/>Implemented and contributed to much of the Peer class logic, including changeOfPreferredNeighbors, optimisticallyUnchokeNeighbor and associated helper functions. This includes the calculation of the data rate for interested peers.<br/>Helped my teammates troubleshoot bugs, including an error in the download incrementation, tracking of preferred neighbors, small logic errors.<br/>                                                                                                                                                                                                                                                                          |
| Jesus Jurado      | jesusjurado@ufl.edu     | This<br/>That<br/>The other<br/>                                                                                                                                                                                                                                                                          |
| Christopher Harm  | harm.c@ufl.edu          | This<br/>That<br/>The other<br/>                                                                                                                                                                                                                                                                          |
