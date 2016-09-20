package client;
import java.io.IOException;
import java.util.Iterator;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
public class Class1 {
   
    
    private final String host;
    private final int port;
    private final String userName;
    private final String password;
    
    public Class1(String host, int port, String userName, String password) {
      this.host = host;
      this.port = port;
      this.userName = userName;
      this.password = password;
    }
    public static void main(String[] args) {
        if (args.length == 0) {
          throw new IllegalArgumentException("Specify the username to be searched with");
        }
        Class1 client = new Class1("ldap://10.213.100.56/",25, "vf-eg\\V15EHassan7", "Voda2014");
        LdapConnection connection=null;
        try {
            connection = client.getConnection();
        } catch (IOException e) {
            // TODO
        } catch (LdapException e) {
            // TODO
        }
        Iterator searchResults=null;
        try {
            searchResults = 
                    client.search(connection, "dc=vf-eg,dc=internal,dc=vodafone,dc=com", "sAMAccountName", "V15EHassan7");
        } catch (LdapException e) {
            // TODO
        }
        if (searchResults.hasNext()) {
          //printBookDetails((Entry) searchResults.next());
            System.out.println("You can login MR " + args[0]);
            
        } else {
          System.out.println("You can login MR " + args[0] + "as the your account doesn't exsist");
        }
        try {
            client.closeConnection(connection);
        } catch (IOException e) {
            // TODO
        } catch (LdapException e) {
            // TODO
        }
    }

    public LdapConnection getConnection() throws LdapException, IOException {
            LdapConnection connection = new LdapNetworkConnection( "localhost", 389 );
    //  LdapConnection connection = new LdapNetworkConnection(host, port);
      connection.setTimeOut(0);
      if(connection.isConnected())
          System.out.println("connection success");
      else
          System.out.println("connection failed");
      connection.bind("uid=" + userName + ",ou=system", password);
      return connection;
    }
    
    public void closeConnection(LdapConnection connection) throws LdapException, IOException {
      connection.unBind();
      connection.close();
    }
    
    public Iterator search(LdapConnection connection, String baseDn, String attributeName, String attributeValue) throws LdapException {
      String filter = "(" + attributeName + "=" + attributeValue + ")";
     // SearchScope searchScope = SearchScope.SUBTREE;
      SearchScope searchScope = SearchScope.ONELEVEL;

        EntryCursor cursor = connection.search(baseDn, filter, searchScope);
        return cursor.iterator();
    }
    

    
    /*  private static void printBookDetails(Entry book) {
      System.out.println("Title: " + book.get("subject").get().toString());
      System.out.println("Author: " + book.get("author").get().toString());
      Attribute synopsis = book.get("synopsis");
      if (synopsis != null) {
        System.out.println("Synopsis: " + synopsis.get().toString());
      }
    }*/

}
