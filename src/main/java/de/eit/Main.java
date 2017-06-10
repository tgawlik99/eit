package de.eit;

import java.io.*;

public class Main
  {
// test
    public static void main(String[] args)
      {
      try
        {
        boolean testmode = false;
        boolean usmode = false;

        if (args.length > 0)
          {
          if (args[0].equals ("test"))
            testmode = true;
          else if (args[0].equals ("us"))
            usmode = true;
          if (args.length > 1 && args[1].equals ("test"))
            testmode = true;
          }
        File folder = new File (".");
        File[] files = folder.listFiles ();
        for (File f : files)
          {
          String orgname = f.getName ();
          if (orgname.indexOf ('.') == -1)
            continue;
          if (!orgname.substring (orgname.lastIndexOf ('.')).toLowerCase ().equals (".eit"))
            continue;
          InputStreamReader in = new InputStreamReader (new FileInputStream (f));
          long length = f.length ();
          char[] content = new char[(int) length];
          long count = 0;
          while (count < length)
            {
            int read = in.read (content);
            if (read == -1)
              break;
            count += read;
            }
          in.close ();

          StringBuilder sb = new StringBuilder ();
          int pos = 12;
          String name = null;
          String season = null;
          while (pos < length - 10)
            {
            if (usmode)
              {
              if ((content[pos] == 0xFFFD || content[pos] == 0xF9) && content[pos+1] == 0x05)
                {
                name = "";
                pos += 2;
                while (pos < length - 10 && !(content[pos] == 0x20 && content[pos+1] == 0x2A))
                  name += content[pos++];
                break;
                }
              else
                pos++;
              }
            else
              {
              if (content[pos] == 0x4D && name == null)
                {
                int evtn_length = content[pos + 5];
                if (evtn_length < 1)
                  break;
                sb.append (content, pos + 7, evtn_length - 1);
                name = sb.toString ();
                pos += 7;
                }
              if (pos > 10 && content[pos - 1] != '(' && content[pos] == 'S' && content[pos + 1] == 't' && content[pos + 2] == 'a' && content[pos + 3] == 'f' && content[pos + 4] == 'f' && content[pos + 5] == 'e' && content[pos + 6] == 'l' && season == null)
                {
                season = "";
                sb = new StringBuilder ();
                int bpos = pos - 3;
                while (content[bpos - 1] != 0x05 && bpos > 1)
                  bpos--;
                if (bpos == 1)
                  break;
                while (bpos < pos - 2)
                  sb.append (content[bpos++]);

                try
                  {
                  if (Integer.parseInt (sb.toString ()) < 10)
                    season = "S0" + sb.toString ();
                  else
                    season = 'S' + sb.toString ();

                  sb = new StringBuilder ();
                  pos += 15;
                  while (pos < length && content[pos] != ':')
                    sb.append (content[pos++]);

                  if (Integer.parseInt (sb.toString ()) < 10)
                    season += "E0" + sb.toString ();
                  else
                    season += 'E' + sb.toString ();
                  } catch (Exception e)
                  {
                  }
                break;
                }
              pos++;
              }
            }
          if (name != null && name != "")
            {
            File[] renamefiles = folder.listFiles ();
            for (File frename : renamefiles)
              {
              String rname = frename.getName ();
              if (rname.indexOf ('.') == -1)
                continue;
              if ((rname.substring(rname.lastIndexOf('.')).equals(".eit")  || rname.substring(rname.lastIndexOf('.')).equals(".is")) &&
                      (!orgname.substring (0, orgname.lastIndexOf ('.')).equals (rname.substring (0, rname.lastIndexOf ('.')))))
                continue;

              if (!orgname.substring (0, orgname.lastIndexOf ('.')).equals (rname.substring (0, rname.indexOf ('.'))))
                continue;
              String filename = name;
              if (season != null && season != "")
                filename += "." + season;
              filename += rname.substring (rname.indexOf ('.'));
              filename  = filename.replaceAll (":", " - ");


                filename  = filename.replaceAll ("Ã¼", "ü");
                filename  = filename.replaceAll ("Ã¶", "ö");
                filename  = filename.replaceAll ("Ã¤", "ä");
                //filename  = filename.replaceAll ("Ã¼", "Ä");
                //filename  = filename.replaceAll ("Ã¼", "Ü");
                //filename  = filename.replaceAll ("Ã¼", "Ö");
                filename  = filename.replaceAll ("Ã¶ÃŸ", "ß");
                filename  = filename.replaceAll ("ÃŸ", "ß");


              if (testmode) {
                System.out.println(filename);
                System.in.read();
                return;
              }
              else
                frename.renameTo (new File (filename));
              }
            }
          }
        }
      catch (Exception e)
          {
          e.printStackTrace ();
          }
      }
}
