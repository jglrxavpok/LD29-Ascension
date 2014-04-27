package org.jglrxavpok.opengl;

public enum TextFormatting
{

	OBFUSCATED("§k"),
	UNDERLINE("§u"),
	ITALIC("§i"),
	RESET("§r");
	
	private String	s;

	TextFormatting(String s)
	{
		this.s =s ;
	}
	
	public String toString()
	{
		return s;
	}
	
	/**
	 * Only escapes the '§' character
	 * @param toEscape
	 * @return
	 */
	public static String escapeString(String toEscape)
	{
	    StringBuilder builder = new StringBuilder("");
	    char[] chars = toEscape.toCharArray();
	    for(int i = 0;i<chars.length;i++)
	    {
	        if(chars[i] == '§')
	        {
	            if(i-1 >= 0)
	            {
	                if(chars[i-1] != '\\')
	                    builder.append('\\');
	            }
	            else
	                builder.append('\\');
	        }
	        builder.append(chars[i]);
	    }
	    return builder.toString();
	}
}
