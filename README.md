# pw-mngr
offline password manager

Initial steps: 

1. Set environment variable: **java -jar ProjectLocation\pw-mngr\out\artifacts\pw_mngr_jar\pw-mngr.jar**
2. Run the app in a **terminal** using **%environmentVarName%**
3. Choose **any** private key
4. Insert the private key and generated IVs in Cryptool 2.1 workspace (PropertiesEncryption.cwm)
5. Run Cryptool to generate encrypted properties values
6. Insert each property in cipher.properties file
7. **Rebuild** the JAR
8. Run the app again, using the **same** private key