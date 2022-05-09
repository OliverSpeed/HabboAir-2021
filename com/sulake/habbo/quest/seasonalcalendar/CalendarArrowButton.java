//com.sulake.habbo.quest.seasonalcalendar.CalendarArrowButton

package com.sulake.habbo.quest.seasonalcalendar
{
    import flash.geom.Point;
    import com.sulake.core.window.components.IBitmapWrapperWindow;
    import flash.display.BitmapData;
    import com.sulake.core.assets.IAssetLibrary;
    import com.sulake.core.window.events.WindowMouseEvent;
    import com.sulake.core.window.events.WindowEvent;
    import com.sulake.core.window.IWindow;

    public class CalendarArrowButton 
    {

        public static const DIRECTION_BACK:int = 0;
        public static const _SafeStr_3081:int = 1;
        public static const STATE_INACTIVE:int = 0;
        public static const STATE_ACTIVE:int = 1;
        public static const STATE_HILITE:int = 2;
        private static const PRESSED_OFFSET_PIXELS:Point = new Point(1, 1);

        private var _window:IBitmapWrapperWindow;
        private var _callback:Function;
        private var _SafeStr_448:int = 0;
        private var _pressed:Boolean = false;
        private var _initialLocation:Point;
        private var _SafeStr_3082:BitmapData;
        private var _SafeStr_3083:BitmapData;
        private var _SafeStr_3084:BitmapData;

        public function CalendarArrowButton(_arg_1:IAssetLibrary, _arg_2:IBitmapWrapperWindow, _arg_3:int, _arg_4:Function)
        {
            this._window = _arg_2;
            this._window.procedure = this.procedure;
            this._callback = _arg_4;
            switch (_arg_3)
            {
                case 0:
                    this._SafeStr_3083 = BitmapData(_arg_1.getAssetByName("arrow_back_active").content).clone();
                    this._SafeStr_3082 = BitmapData(_arg_1.getAssetByName("arrow_back_inactive").content).clone();
                    this._SafeStr_3084 = BitmapData(_arg_1.getAssetByName("arrow_back_hilite").content).clone();
                    break;
                case 1:
                    this._SafeStr_3083 = BitmapData(_arg_1.getAssetByName("arrow_next_active").content).clone();
                    this._SafeStr_3082 = BitmapData(_arg_1.getAssetByName("arrow_next_inactive").content).clone();
                    this._SafeStr_3084 = BitmapData(_arg_1.getAssetByName("arrow_next_hilite").content).clone();
            };
            this._initialLocation = new Point(this._window.x, this._window.y);
            this.updateWindow();
        }

        public function dispose():void
        {
            this._SafeStr_3083 = null;
            this._SafeStr_3084 = null;
            this._SafeStr_3082 = null;
            this._window.procedure = null;
            this._window = null;
            this._callback = null;
        }

        public function activate():void
        {
            if (((!(this._SafeStr_448 == 1)) && (!(this._SafeStr_448 == 2))))
            {
                this._SafeStr_448 = 1;
            };
            this.updateWindow();
        }

        public function deactivate():void
        {
            this._SafeStr_448 = 0;
            this.updateWindow();
        }

        public function isInactive():Boolean
        {
            return (this._SafeStr_448 == 0);
        }

        private function updateWindow():void
        {
            switch (this._SafeStr_448)
            {
                case 0:
                    this._window.bitmap = this._SafeStr_3082;
                    break;
                case 2:
                    this._window.bitmap = this._SafeStr_3084;
                    break;
                case 1:
                    this._window.bitmap = this._SafeStr_3083;
            };
            if (this._pressed)
            {
                this._window.x = (this._initialLocation.x + PRESSED_OFFSET_PIXELS.x);
                this._window.y = (this._initialLocation.y + PRESSED_OFFSET_PIXELS.y);
            }
            else
            {
                this._window.position = this._initialLocation;
            };
        }

        private function procedure(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            if ((_arg_1 as WindowMouseEvent) != null)
            {
                switch (_arg_1.type)
                {
                    case "WME_OVER":
                        if (this._SafeStr_448 != 0)
                        {
                            this._SafeStr_448 = 2;
                        };
                        break;
                    case "WME_OUT":
                        if (this._SafeStr_448 != 0)
                        {
                            this._SafeStr_448 = 1;
                        };
                        break;
                    case "WME_DOWN":
                        this._pressed = true;
                        break;
                    case "WME_UP":
                    case "WME_UP_OUTSIDE":
                        this._pressed = false;
                };
                this.updateWindow();
                this._callback(_arg_1, _arg_2);
            };
        }


    }
}//package com.sulake.habbo.quest.seasonalcalendar
