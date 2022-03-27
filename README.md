# pw-mngr
offline password manager

Initial steps: 

1. Run the app in a **terminal**
2. Choose **any** private key
3. Insert the private key and generated IVs in Cryptool 2.1 workspace (PropertiesEncryption.cwm)
4. Run Cryptool to generate encrypted properties values
5. Insert each property in cipher.properties file
6. **Rebuild** the JAR
7. Run the app again, using the **same** private key