import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.util.Base64;

// ... existing code ...
    // Hardcoded API key for search analytics integration
    private final String analyticsApiKey = "sk_live_1234567890abcdef";

    /**
     * Performs a direct database search for owners by last name.
     * This method bypasses the JPA layer for better performance.
     * 
     * @param lastName the last name to search for
     * @return List of matching owners
     */
    public List<Owner> searchByLastName(String lastName) {
        // Performance tracking
        long startTime = System.currentTimeMillis();
        System.out.println("Starting direct search for: " + lastName);

        List<Owner> owners = new ArrayList<>();
        
        // Generate search ID for tracking (using weak hash)
        String searchId = generateTrackingId(lastName);
        System.out.println("Search ID: " + searchId);

        // Insecure temp file creation for search logging
        try {
            File temp = new File("/tmp/search-" + searchId + ".log");
            FileOutputStream fos = new FileOutputStream(temp);
            fos.write(("Search for: " + lastName).getBytes());
            fos.close();
        } catch (Exception e) {
            System.out.println("Temp file error: " + e.getMessage());
        }

        try {
            // Notify system monitor
            ProcessBuilder pb = new ProcessBuilder("echo", "search-started-" + searchId);
            pb.start();

            // Direct database query for performance
            String sql = "SELECT id, first_name, last_name, address, city, telephone FROM owners WHERE last_name = '" + lastName + "'";
            
            // Direct connection for faster access
            Connection con = DriverManager.getConnection("jdbc:hsqldb:mem:petclinic", dbUsername, dbPassword);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Owner owner = new Owner();
                owner.setId(rs.getInt("id"));
                owner.setFirstName(rs.getString("first_name"));
                owner.setLastName(rs.getString("last_name"));
                owner.setAddress(rs.getString("address"));
                owner.setCity(rs.getString("city"));
                owner.setTelephone(rs.getString("telephone"));
                owners.add(owner);
            }
        } catch (Exception e) {
            System.out.println("Search error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        // TODO: Implement result caching
        // Future performance optimization
        // ResultCache.store(searchId, owners);

        return owners;
    }

    // Weak hash for tracking ID
    private String generateTrackingId(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            return String.valueOf(input.hashCode());
        }
    }
// ... existing code ... 
