package com.disablerouting.instructions;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.Map;

public class DirectionInstruction {

  public enum DIRECTIONS {
    LEFT(0),
    RIGHT(1),
    SHARP_LEFT(2),
    SHARP_RIGHT(3),
    SLIGHT_LEFT(4),
    SLIGHT_RIGHT(5),
    STRAIGHT(6),
    ENTER_ROUND_ABOUT(7),
    EXIT_ROUND_ABOUT(8),
    UTURN(9),
    GOAL(10),
    DEPART(11),
    KEEPLEFT(12),
    KEEPRIGHT(13),
    MID_WAY(14); // My own define type

    private int mType;
    private static Map map= new HashMap();

    DIRECTIONS(int type) {
      this.mType=type;
    }

    public int getType() {
      return mType;
    }

    static {
      for (DIRECTIONS pageType : DIRECTIONS.values()) {
        map.put(pageType.mType, pageType);
      }
    }
    public static DIRECTIONS valueOf(int pageType) {
      return (DIRECTIONS) map.get(pageType);
    }
  }

    public static Drawable getEnumDrawable(Context context, int type,int coordinateSize) {
      DIRECTIONS directions= DIRECTIONS.valueOf(type);
      switch (directions) {
        case LEFT:
          return getImage(context,"ic_left");
        case RIGHT:
          return getImage(context,"ic_right");
        case SHARP_LEFT:
          return getImage(context,"ic_left");
        case SHARP_RIGHT:
          return getImage(context,"ic_right");
        case SLIGHT_LEFT:
          return getImage(context,"ic_slight_left_turn");
        case SLIGHT_RIGHT:
          return getImage(context,"ic_slight_right_turn");
        case STRAIGHT:
          return getImage(context,"ic_up");
        case ENTER_ROUND_ABOUT:
          return getImage(context,"ic_round");
        case EXIT_ROUND_ABOUT:
          return getImage(context,"ic_round");
        case UTURN:
          return getImage(context,"ic_uturn");
        case GOAL:
          if(coordinateSize==2) {
            return getImage(context, "ic_dest_b");
          }else {
            return getImage(context, "ic_mid_c");
          }
        case DEPART:
          return getImage(context,"ic_source_a");
        case KEEPLEFT:
          return getImage(context,"ic_keep_left");
        case KEEPRIGHT:
          return getImage(context,"ic_keep_right");
        case MID_WAY:
          if(coordinateSize==3) {
            return getImage(context, "ic_dest_b");
          }
        default:
          return null;
      }
    }

  private static Drawable getImage(Context context, String name) {
      return context.getResources().getDrawable(context.getResources().getIdentifier(name, "drawable", context.getPackageName()));
    }


}