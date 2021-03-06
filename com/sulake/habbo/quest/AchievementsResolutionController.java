//com.sulake.habbo.quest.AchievementsResolutionController

package com.sulake.habbo.quest
{
    import com.sulake.core.runtime.IDisposable;
    import com.sulake.core.window.components.IFrameWindow;
    import __AS3__.vec.Vector;
    import com.sulake.habbo.communication.messages.incoming.inventory.achievements.AchievementResolutionData;
    import com.sulake.core.window.components.IItemGridWindow;
    import com.sulake.habbo.communication.messages.outgoing.game.lobby.GetResolutionAchievementsMessageComposer;
    import com.sulake.habbo.communication.messages.incoming.notifications.AchievementLevelUpData;
    import com.sulake.habbo.communication.messages.incoming.inventory.achievements.AchievementData;
    import com.sulake.habbo.communication.messages.outgoing.game.lobby.ResetResolutionAchievementMessageComposer;
    import com.sulake.habbo.window.utils._SafeStr_126;
    import com.sulake.core.window.events.WindowEvent;
    import com.sulake.core.window.components.IWidgetWindow;
    import com.sulake.habbo.window.widgets.ICountdownWidget;
    import com.sulake.core.window.IWindow;
    import com.sulake.core.window.events.WindowMouseEvent;
    import com.sulake.core.window.IWindowContainer;
    import com.sulake.habbo.window.widgets.IBadgeImageWidget;
    import com.sulake.core.window.components.IStaticBitmapWrapperWindow;

    public class AchievementsResolutionController implements IDisposable 
    {

        private static const _SafeStr_3113:String = "header_button_close";
        private static const _SafeStr_3118:String = "save_button";
        private static const _SafeStr_3114:String = "cancel_button";
        private static const _SafeStr_3119:String = "ok_button";
        private static const ELEM_DISABLED_INFO:String = "disabled.reason";

        private var _questEngine:HabboQuestEngine;
        private var _window:IFrameWindow;
        private var _progressView:AchievementResolutionProgressView;
        private var _completedView:AchievementResolutionCompletedView;
        private var _stuffId:int;
        private var _SafeStr_1999:Vector.<AchievementResolutionData>;
        private var _selectedAchievementId:int = -1;
        private var _endTime:int = -1;

        public function AchievementsResolutionController(_arg_1:HabboQuestEngine)
        {
            this._questEngine = _arg_1;
        }

        public function dispose():void
        {
            var _local_1:IItemGridWindow;
            this._questEngine = null;
            if (this._window)
            {
                _local_1 = (this._window.findChildByName("achievements") as IItemGridWindow);
                if (_local_1)
                {
                    _local_1.destroyGridItems();
                };
                if (this._progressView)
                {
                    this._progressView.dispose();
                    this._progressView = null;
                };
                if (this._completedView)
                {
                    this._completedView.dispose();
                    this._completedView = null;
                };
                this._window.dispose();
                this._window = null;
            };
        }

        public function get disposed():Boolean
        {
            return (this._questEngine == null);
        }

        public function onResolutionAchievements(_arg_1:int, _arg_2:Vector.<AchievementResolutionData>, _arg_3:int):void
        {
            this._stuffId = _arg_1;
            this._SafeStr_1999 = _arg_2;
            this._endTime = _arg_3;
            if (_arg_2.length == 0)
            {
                return;
            };
            this.refresh();
            this._window.visible = true;
            this._selectedAchievementId = this._SafeStr_1999[0].achievementId;
            this.populateAchievementGrid();
            this.selectAchievement(this._selectedAchievementId);
        }

        public function onResolutionProgress(_arg_1:int, _arg_2:int, _arg_3:String, _arg_4:int, _arg_5:int, _arg_6:int):void
        {
            if (!this._progressView)
            {
                this._progressView = new AchievementResolutionProgressView(this);
            };
            this._progressView.show(_arg_1, _arg_2, _arg_3, _arg_4, _arg_5, _arg_6);
        }

        public function onResolutionCompleted(_arg_1:String, _arg_2:String):void
        {
            if (!this._completedView)
            {
                this._completedView = new AchievementResolutionCompletedView(this);
            };
            this._completedView.show(_arg_2, _arg_1);
        }

        public function onLevelUp(_arg_1:AchievementLevelUpData):void
        {
            if ((((this._progressView) && (this._progressView.visible)) && (_arg_1.type == this._progressView.achievementId)))
            {
                this._questEngine.send(new GetResolutionAchievementsMessageComposer(this._progressView.stuffId, 0));
            };
        }

        public function onAchievement(_arg_1:AchievementData):void
        {
            if ((((this._progressView) && (this._progressView.visible)) && (_arg_1.achievementId == this._progressView.achievementId)))
            {
                this._questEngine.send(new GetResolutionAchievementsMessageComposer(this._progressView.stuffId, 0));
            };
        }

        public function resetResolution(_arg_1:int):void
        {
            var stuffId:int;
            var title:String;
            var summary:String;
            stuffId = _arg_1;
            if ((((this._progressView) && (this._progressView.visible)) && (stuffId == this._progressView.stuffId)))
            {
                title = "${resolution.reset.confirmation.title}";
                summary = "${resolution.reset.confirmation.text}";
                this._questEngine.windowManager.confirm(title, summary, 0, function (_arg_1:_SafeStr_126, _arg_2:WindowEvent):void
                {
                    _arg_1.dispose();
                    if (_arg_2.type == "WE_OK")
                    {
                        _questEngine.send(new ResetResolutionAchievementMessageComposer(stuffId));
                        _questEngine.send(new GetResolutionAchievementsMessageComposer(_progressView.stuffId, 0));
                    };
                });
            };
        }

        private function refresh():void
        {
            if (this._window == null)
            {
                this.prepareWindow();
            };
            var _local_1:IWidgetWindow = IWidgetWindow(this._window.findChildByName("countdown_widget"));
            var _local_2:ICountdownWidget = ICountdownWidget(_local_1.widget);
            _local_2.seconds = this._endTime;
            _local_2.running = true;
        }

        private function prepareWindow():void
        {
            if (this._window != null)
            {
                return;
            };
            this._window = IFrameWindow(this._questEngine.getXmlWindow("AchievementsResolutions"));
            this._window.findChildByTag("close").procedure = this.onWindowClose;
            this._window.center();
            this._window.visible = true;
            this.addClickListener("header_button_close");
            this.addClickListener("save_button");
            this.addClickListener("cancel_button");
        }

        private function onWindowClose(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            if (_arg_1.type == "WME_CLICK")
            {
                this.close();
            };
        }

        private function addClickListener(_arg_1:String):void
        {
            var _local_2:IWindow = this._window.findChildByName(_arg_1);
            if (_local_2 != null)
            {
                _local_2.addEventListener("WME_CLICK", this.onMouseClick);
            };
        }

        private function onMouseClick(_arg_1:WindowMouseEvent):void
        {
            var title:String;
            var summary:String;
            var event:WindowMouseEvent = _arg_1;
            switch (event.target.name)
            {
                case "header_button_close":
                case "cancel_button":
                    this.close();
                    return;
                case "ok_button":
                    return;
                case "save_button":
                    title = "${resolution.confirmation.title}";
                    summary = "${resolution.confirmation.text}";
                    this.close();
                    this._questEngine.windowManager.confirm(title, summary, 0, function (_arg_1:_SafeStr_126, _arg_2:WindowEvent):void
                    {
                        _arg_1.dispose();
                        if (_arg_2.type == "WE_OK")
                        {
                            _questEngine.send(new GetResolutionAchievementsMessageComposer(_stuffId, _selectedAchievementId));
                        }
                        else
                        {
                            _window.visible = true;
                        };
                    });
                    return;
            };
        }

        public function isVisible():Boolean
        {
            return ((this._window) && (this._window.visible));
        }

        public function close():void
        {
            if (this._window)
            {
                this._window.visible = false;
            };
        }

        private function populateAchievementGrid():void
        {
            var _local_1:IWindowContainer;
            var _local_4:AchievementResolutionData;
            var _local_2:IItemGridWindow = (this._window.findChildByName("achievements") as IItemGridWindow);
            _local_2.destroyGridItems();
            var _local_3:IWindow = this._questEngine.getXmlWindow("AchievementSimple");
            for each (_local_4 in this._SafeStr_1999)
            {
                _local_1 = (_local_3.clone() as IWindowContainer);
                _local_1.id = _local_4.achievementId;
                this.refreshBadgeImage(_local_1, _local_4);
                _local_1.findChildByName("bg_region").procedure = this.onSelectAchievementProc;
                _local_1.findChildByName("bg_selected_bitmap").visible = false;
                _local_2.addGridItem(_local_1);
            };
        }

        private function hiliteGridItem(_arg_1:int, _arg_2:Boolean):void
        {
            var _local_3:IItemGridWindow = (this._window.findChildByName("achievements") as IItemGridWindow);
            var _local_4:IWindowContainer = (_local_3.getGridItemByID(_arg_1) as IWindowContainer);
            if (_local_4)
            {
                _local_4.findChildByName("bg_selected_bitmap").visible = _arg_2;
            };
        }

        private function selectAchievement(_arg_1:int):void
        {
            if (this._selectedAchievementId != -1)
            {
                this.hiliteGridItem(this._selectedAchievementId, false);
            };
            var _local_2:AchievementResolutionData = this.findAchievement(_arg_1);
            if (_local_2 == null)
            {
                return;
            };
            this._selectedAchievementId = _arg_1;
            this.hiliteGridItem(this._selectedAchievementId, true);
            this._window.findChildByName("achievement.name").caption = this._questEngine.localization.getBadgeName(_local_2.badgeId);
            this._window.findChildByName("achievement.description").caption = this._questEngine.localization.getBadgeDesc(_local_2.badgeId);
            this._window.findChildByName("achievement.level").caption = _local_2.level.toString();
            this._questEngine.localization.registerParameter("resolution.achievement.target.value", "level", _local_2.requiredLevel.toString());
            this.refreshBadgeImageLarge(_local_2);
            if (_local_2.enabled)
            {
                this.enable();
            }
            else
            {
                this.disable(_local_2.state);
            };
        }

        private function disable(_arg_1:int):void
        {
            this._window.setVisibleChildren(false, ["save_button"]);
            this._window.setVisibleChildren(true, ["disabled.reason"]);
            this._window.findChildByName("disabled.reason").caption = (("${resolution.disabled." + _arg_1) + "}");
        }

        public function enable():void
        {
            this._window.setVisibleChildren(true, ["save_button"]);
            this._window.setVisibleChildren(false, ["disabled.reason"]);
        }

        private function onSelectAchievementProc(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            if (_arg_1.type != "WME_CLICK")
            {
                return;
            };
            this.selectAchievement(_arg_2.parent.id);
        }

        private function refreshBadgeImage(_arg_1:IWindowContainer, _arg_2:AchievementResolutionData):void
        {
            var _local_3:IWidgetWindow = (_arg_1.findChildByName("achievement_pic_bitmap") as IWidgetWindow);
            var _local_4:IBadgeImageWidget = (_local_3.widget as IBadgeImageWidget);
            if (_arg_2 == null)
            {
                _local_3.visible = false;
                return;
            };
            IStaticBitmapWrapperWindow(IWindowContainer(_local_3.rootWindow).findChildByName("bitmap")).assetUri = "common_loading_icon";
            _local_4.badgeId = _arg_2.badgeId;
            _local_4.greyscale = (!(_arg_2.enabled));
            _local_3.visible = true;
        }

        private function refreshBadgeImageLarge(_arg_1:AchievementResolutionData):void
        {
            var _local_2:IWidgetWindow = (this._window.findChildByName("achievement_badge") as IWidgetWindow);
            var _local_3:IBadgeImageWidget = (_local_2.widget as IBadgeImageWidget);
            IStaticBitmapWrapperWindow(IWindowContainer(_local_2.rootWindow).findChildByName("bitmap")).assetUri = "common_loading_icon";
            _local_3.badgeId = _arg_1.badgeId;
            _local_3.greyscale = (!(_arg_1.enabled));
            _local_2.visible = true;
        }

        private function findAchievement(_arg_1:int):AchievementResolutionData
        {
            var _local_2:AchievementResolutionData;
            for each (_local_2 in this._SafeStr_1999)
            {
                if (_local_2.achievementId == _arg_1)
                {
                    return (_local_2);
                };
            };
            return (null);
        }

        public function get questEngine():HabboQuestEngine
        {
            return (this._questEngine);
        }


    }
}//package com.sulake.habbo.quest
