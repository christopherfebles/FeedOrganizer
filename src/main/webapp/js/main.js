var mainJS = (function($) {

	var pub = {};

	//Public Method
	pub.onSubmit = function() {
		//Move list into hidden field
		var listItems = "";
		$("#listMembersList li").filter(":not([id*='blank_'])").each(function(index) {
			listItems = listItems + "," + $(this).attr("id");
		});
		$("#idList").val(listItems);
		return true;
	};
	
	//Public Method
	pub.onMainLoad = function() {
		$.ajax({
			url: "getFriendsList.do",
			dataType: "json",
			async: false,
			success: function(data) {
				for (var i = 0; i < data.length; i++) { 
					var card = createUserCard( data[i], "allFriendsListItem" );
					$("#allFriendsList").append(card);
				}
				//Insert self at top of list
				$.getJSON("getUserData.do",
						function(data) {
					var card = createUserCard( data, "selfItem" );
					$("#allFriendsList").prepend(card);
				});
			}
		});

		$.getJSON("getSubscribedLists.do",
				function(data) {
			for (var i = 0; i < data.length; i++) { 
				var option = createOption( data[i] );
				$("#listDropdown").append(option);
			}
			var listId = $( 'listId' ).val();
			$("#listDropdown").val(listId);
			$("#listDropdown").change();
		});

		$("#listDropdown").change(function() {
			var id = $("#listDropdown").val();

			if ( id === "-1" ) {
				$("#listMembersList").empty();
				return;
			}

			$("#listMembersList").empty();
			$("#allFriendsList li").removeClass("cardInUse");
			$("#allFriendsList li td").children(".delTags").children().unwrap();
			$.ajax({
				url: "getListMembers.do",
				data: { listId : id },
				success:
					function(data) {
					for (var i = 0; i < data.length; i++) { 
						var card = createUserCard( data[i], "listMemberItem" );
						$("#listMembersList").append(card);
						$("#allFriendsList li#" + data[i].id).addClass("cardInUse");
						$("#allFriendsList li#" + data[i].id + " td").children().wrap("<del class='delTags'></del>");
					}
					normalizeLists();
				},
				error: function (xhr, ajaxOptions, thrownError) {
					alert(xhr.status);
					alert(thrownError);
				}
			});
		});

		$( "#allFriendsList, #listMembersList" ).sortable({
			connectWith: ".connectedSortable",
			placeholder: "placeholder",
			forcePlaceholderSize: true,
			receive: function(event, ui) {
				normalizeLists();
			}
		}).disableSelection();
	};

	//Private Method
	function normalizeLists() {
		//Compare list lengths
		var friendsListLength = $("#allFriendsList").children("li").length;
		var memberListLength = $("#listMembersList").children("li").length;
		if ( friendsListLength > memberListLength ) {
			var difference = friendsListLength - memberListLength;
			for ( var i = 0; i < difference; i++ ) {
				var card = createEmptyUserCard(i);
				$("#listMembersList").append('<li id="blank_' + i + '" class="friendCard emptyCard placeholder"></li>');
			}
		} else if ( friendsListLength < memberListLength ) {
			var difference = memberListLength - friendsListLength;
			for ( var i = 0; i < difference; i++ ) {
				var idSelector = "#" + $("#listMembersList").children('li[id*="blank_"]')[i].id;
				$(idSelector).remove();
			}
		}
	}

	//Private Method
	function createEmptyUserCard(id) {
		var table = '<table><tr><td><img src="' + '' + '" height="73" width="73" class="twitter-avatar"/></td><td><div class="userName">' + 
		'' + '</div><div class="screenName">' + '' + '</div></td></tr></table>';

		var retVal = '<li id="' + id + '" class="friendCard listMemberItem">' + table + '</li>';

		return retVal;
	}

	//Private Method
	function createUserCard(user, cssClass) {
		//Create a "card" containing the data for the given Twitter user (JSON object)
		var table = '<table><tr><td><img src="' + user.profile_image_url_https + 
		'" height="73" width="73" class="twitter-avatar"/></td><td><div class="userName">' + user.name + '</div><div class="screenName">@' + user.screen_name + 
		'</div></td></tr></table>';

		var retVal = '<li id="' + user.id + '" class="friendCard emptyCard ' + cssClass + '">' + table + '</li>';

		return retVal;
	}

	//Private Method
	function createOption(list) {
		var option = "<option value='" + list.id_str + "'>" + list.name + "</option>";
		return option;
	}

	return pub;
}(jQuery));