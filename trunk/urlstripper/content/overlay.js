var urlstripper = {
  onLoad: function() {
    // initialization code
    this.initialized = true;
    this.strings = document.getElementById("urlstripper-strings");
    document.getElementById("contentAreaContextMenu").addEventListener("popupshowing", showContextMenu, false);
  },

  showContextMenu: function() {
    // show or hide the menuitem based on what the context menu is on
    // see http://kb.mozillazine.org/Adding_items_to_menus
    document.getElementById("context-urlstripper").hidden = !gContextMenu.onLink;
  },
  onMenuItemCommand: function() {
	var oldurl = gContextMenu.linkURL
			.replace(/%3A/g,":")
			.replace(/%2F/g,"/")
			.replace(/%3F/g,"?")
			.replace(/%3D/g,"=")
			.replace(/%2C/g,",")
			.replace(/%23/g,"#");
	if(oldurl.substring(7).indexOf("http://") > 0)
		openURL(oldurl.substring(oldurl.substring(7).indexOf("http://") + 7));
  },

};
window.addEventListener("load", onLoad, false);
