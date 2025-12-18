import java.util.Iterator;
import java.util.List;

public class DecryptTest {
   public DecryptTest() {
   }
   public static void main(String[] var0) {
      List<String> var1 = Utils.readFile("data/STUDENTS/2025-2026/1stYear/COE/BSEE/A/NW-25-0001/NW-25-0001_info.txt");
      if (var1 == null) {
         System.out.println("Cannot read file!");
      } else {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            String var4 = Utils.decryptEachField(var3);
            System.out.println(var4);
         }

      }
   }
}


