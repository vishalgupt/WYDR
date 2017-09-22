// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package wydr.sellers.acc;

import java.util.regex.Pattern;

public class ValidationUtil
{
    private static Pattern ALPHBETS= Pattern.compile("^\\w+( \\w+)*$");
    private static Pattern ALPHANUMERIC = Pattern.compile("[A-Za-z0-9.@_-]+");
    private static Pattern ALPHANUMERIC_NAME = Pattern.compile("[A-Za-z0-9. ]+");
    private static Pattern ALPHANUMERIC_ZIPCODE = Pattern.compile("[A-Za-z0-9.-]+");
    private static Pattern Age = Pattern.compile("^([0-9]*)$");
    private static String EmailExpression;
    private static Pattern EmailPattern;
    private static Pattern MOBILNO = Pattern.compile("^([0-9]*)$");
    private static Pattern PASSWORD = Pattern.compile("^[a-z0-9_-[!#$%&]]{5,15}$");
    private static Pattern Time = Pattern.compile("^([0-9]*)$");
    private static Pattern USERNAME = Pattern.compile("^[a-z0-9_]{2,15}$");
    private static Pattern NUMBER = Pattern.compile("^[0-9]+(\\.[0-9]{1,2})?$");
    private static Pattern MOBILE_NO = Pattern.compile("^[123456789]\\d{9}$");
    private static Pattern NUMERIC = Pattern.compile("^[0-9]+$");

    public ValidationUtil()
    {
    }

    public static boolean isNull(String s)
    {
        return s == null || s.trim().length() <= 0;
    }

    public static boolean isNumeric(String s)
    {
        if (s == null)
        {
            return false;
        } else
        {
            return NUMERIC.matcher(s).matches();
        }
    }
    public static boolean isValidMobile(String s)
    {
        if (s == null)
        {
            return false;
        } else
        {
            return MOBILE_NO.matcher(s).matches();
        }
    }

    public static boolean isValidNumber(String s)
    {
        if (s == null)
        {
            return false;
        } else
        {
            return NUMBER.matcher(s).matches();
        }
    }
    public static boolean isValidNumber2(String s)
    {
        if (s == null || s.equalsIgnoreCase(""))
        {
            return true;
        } else
        {
            return NUMBER.matcher(s).matches();
        }
    }
    public static boolean isValidEmailId(String s)
    {
        if (s == null)
        {
            return false;
        } else
        {
            return EmailPattern.matcher(s).matches();
        }
    }
    public static boolean isValidText(String s)
    {
        return ALPHBETS.matcher(s).matches();
    }

    public static boolean isValidMobileNumber(String s)
    {
        if (s == null)
        {
            return false;
        } else
        {
            return MOBILNO.matcher(s).matches();
        }
    }

    public static boolean isValidName(String s)
    {
        if (s == null)
        {
            return false;
        } else
        {
            return ALPHANUMERIC_NAME.matcher(s).matches();
        }
    }

    public static boolean isValidPassword(String s)
    {
        if (s == null)
        {
            return false;
        } else
        {
            return PASSWORD.matcher(s).matches();
        }
    }

    public static boolean isValidTime(String s)
    {
        if (s == null)
        {
            return false;
        } else
        {
            return Time.matcher(s).matches();
        }
    }

    public static boolean isValidUserName(String s)
    {
        if (s == null)
        {
            return false;
        } else
        {
            return USERNAME.matcher(s).matches();
        }
    }

    public static boolean zipcodeValidation(String s)
    {
        if (s == null)
        {
            return false;
        } else
        {
            return ALPHANUMERIC_ZIPCODE.matcher(s).matches();
        }
    }

    static 
    {
        EmailExpression = "(?:[a-z0-9!#$%\\&'*+/=?\\^_`{|}~-]+(?:\\.[a-z0-9!#$%\\&'*+/=?\\^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        EmailPattern = Pattern.compile(EmailExpression, 2);
    }
}
