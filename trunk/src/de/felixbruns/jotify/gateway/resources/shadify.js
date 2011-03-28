/* Jotify gateway. */
				var jotify;
				var cache;
				var session;
				
				/* HTML5 audio element. */
				var audio;
				
				/* Tracks and queue. */
				var currentTracks;
				var currentTrack;
				var queuedTracks;

				/* Display an error message. */
				var error = function(message){
					$('#error-label').html(message);
					
					$('#info-label').hide();
					$('#success-label').hide();
					$('#error-label').show();
				};
				
				/* Display a success message. */
				var success = function(message){
					$('#success-label').html(message);
					
					$('#info-label').hide();
					$('#error-label').hide();
					$('#success-label').show();
				};
				
				/* Display an info message. */
				var info = function(message){
					$('#info-label').html(message);
					
					$('#error-label').hide();
					$('#success-label').hide();
					$('#info-label').show();
				};
				
				/* Append a track to the table. */
				var appendTrack = function(index, track){
					$('#search-table > tbody').append(
						'<tr>' +
							'<td>' +
								'<a id="track-' + index + '" href="#" rel="' + index + '">' + track.title + '</a>' +
							'</td>' +
							'<td>' +
								'<a id="artist-' + index + '" href="#" rel="' + index + '">' + track.artist + '</a>' +
							'</td>' +
							'<td style="text-align: right;">' + formatSeconds(track.length / 1000) + '</td>' +
							'<td class="popularity-indicator" style="background-image: ' + popularityIndicator(track.popularity) + ';">' +
								'<span style="display: none;">' + track.popularity + '</span>' +
							'</td>' +
							'<td>' +
								'<a id="album-' + index + '" href="#" rel="' + index + '">' + track.album + '</a>' +
							'</td>' +
						'</tr>'
					);
				};
				
				/* Append a track to a disc table. */
				var appendTrackToDisc = function(disc, index, track){
					$('#disc-' + disc).append(
						'<tr>' +
							'<td>' + track['track-number'] + '</td>' +
							'<td>' +
								'<a id="track-' + index + '" href="#" rel="' + index + '">' + track.title + '</a>' +
							'</td>' +
							'<td style="text-align: right;">' + formatSeconds(track.length / 1000) + '</td>' +
							'<td>' +
								'<div class="popularity-indicator" style="background-image: ' + popularityIndicator(track.popularity) + ';"></div>' +
							'</td>' +
						'</tr>'
					);
				};

				
				/* Play a track via HTML5 audio. */
				var playTrack = function(track){
					/* Set current track. */
					currentTrack = track;
					
					/* Play selected track (play() will be called in 'canplay' event handler). */
					if($('#play-on-server-checkbox').is(':checked')){
						jotify.playTrack(track, session);

						
					}
					else{
						audio.src = jotify.stream(track, session);
						audio.load();
						
						
					}
					
					
					/* Show now playing label. */
					$('#playing-label').show();
					$('#playing-label').html(
						'<h4 title="' + track.title + '">' + track.title.toString().truncate() + '</h4><br />' +
						track.artist + ' (' + track.album + ')'
					);
					
					/* Show progress. */
					$('#progress-label').show();
					$('#progress-label').html(
						'00:00 / ' + formatSeconds(track.length / 1000)
					);
					
					/* Show album cover. */
					$('#cover-label').show();
					$('#cover-label').attr('src', jotify.image(track.cover, session));
					
					
				};
				
				/* Load user info. */
				var loadUser = function(callback){
					jotify.user(session, {
						success: callback,
						error: function(data){
							loadUser(callback);
						}
					});
				};
				
				var loadedUser = function(data){
					//$('body').prepend(prettyPrint(data));
				};
				

				/* Draw audio progress bar. */
				var drawProgress = function(position, length){
					var canvas    = document.getElementById('progress-bar');
					var ctx       = canvas.getContext("2d");
					var progress  = position / length;
					var seconds   = formatSeconds(position);
					var remaining = '-' + formatSeconds(length - position);
					
					ctx.globalCompositeOperation = 'source-over';
					
					ctx.clearRect(0, 0, canvas.width, canvas.height);
					
					ctx.shadowBlur  = 2.0;
					ctx.shadowColor = "#003E88";
					ctx.fillStyle   = "#003E88";
					
					ctx.fillRect(2, 2, (canvas.width - 4) * progress, canvas.height - 4);
					
					ctx.globalCompositeOperation = 'xor';
					
					ctx.shadowBlur  = 0.0;
					ctx.shadowColor = "transparent black";
					
					ctx.textBaseline = 'middle';
					ctx.fillText(seconds, 5, canvas.height / 2);
					ctx.fillText(remaining, canvas.width - ctx.measureText(remaining).width - 5, canvas.height / 2);
				};
				
				/*
				 * 
				 * DOM READY . 
				 * JQUERY document.ready!
				 * 
				 * JQUERY START DOM.
				 * 
				 */
				$(document).ready(function(){
					/* Create Jotify object. */
					jotify = new Jotify({});
					cache  = new Cache('jotify');
					
					//cache.clear();
					
					/* Try to auto-login. */
					if((session = localStorage['jotify-session']) != null){
						jotify.check(session, {
							success: function(data){
								success('Automatically logged in!');
								
								//loadUser();
								
								$('#main-panel').show();
								$('#play-on-server').show();
								$('#info-panel').show();
								$('#toplist-container').show();
							},
							error: function(data){
								info('Please login using a premium account!');
								
								$('#login-panel').show();
							},
						});
					}
					else{
						info('Please login using a premium account!');
						
						$('#login-panel').show();
					}
					
					/* Get audio element. */
					audio = document.getElementById('audio');
					
					/* Bind to audio element events. */
					$(audio).bind('canplay', function(){
						audio.play();
					});
					$(audio).bind('timeupdate', function(){						
						drawProgress(audio.currentTime, currentTrack.length / 1000);
					});
					$(audio).bind('ended', function(){
						drawProgress(currentTrack.length / 1000, currentTrack.length / 1000);
						
						playTrack(queuedTracks.shift());
					});
					
					
					
					/* Make table sortable. */
					$('#search-table').tablesorter();
					
					
					
					

					/* Login handler. */
					$('#login').click(function(event){
						jotify.login({username: $('#username').val(), password: $('#password').val()}, {
							success: function(data){
								session = data.session;
								
								localStorage['jotify-session'] = session;
								
								success('Successfully logged in!');
								
								//loadUser();

								$('#login-panel').hide();
								$('#main-panel').show();
								$('#play-on-server').show();
								$('#info-panel').show();
								$('#toplist-container').show();
								
							},
							error: error
						});
						
						event.preventDefault();
					});
					
					/*Toplist handler*/
					$('#toplist-btn').live('click', function(event){
						var target = $(event.target);
						
						var toplistregion = $("#toplist-country").val();

						jotify.toplist({type: "track", region: toplistregion}, session, {
							success: function(data){
								$('#browse-album').hide();
								$('#search-table').show();
								/* Clear track table. */
								$('#search-table > tbody').empty();
								
								/* Get tracks from search result. */
								currentTracks = data.toplist.tracks;
								
								if(typeof(currentTracks.track) == 'undefined'){
									currentTracks = [];
								}
								else if(currentTracks.track instanceof Array){
									currentTracks = currentTracks.track;
								}
								else{
									currentTracks = [currentTracks.track];
								}
								
								
								
								/* Loop over tracks and add them to the table. */
								for(var i in currentTracks){
									appendTrack(i, currentTracks[i]);
								}
								
								/* Trigger update event (tablesorter). */
								$('#search-table').trigger('update');
								
								info('Toplist fetched.');
							},
							error: error
						});
						
						event.preventDefault();
					});
					
					
					/* Search handler. */
					$('#search, #did-you-mean').live('click', function(event){
						var target = $(event.target);
						
						if(target.is('a')){
							$('#query').val(target.attr('rel'));
						}
						
						info("Searching for '" + $('#query').val() + "'... <img src=\"images/load.gif\" />");
						
						jotify.search({query: $('#query').val()}, session, {
							success: function(data){
								$('#browse-album').hide();
								$('#search-table').show();
								
								/* Clear track table. */
								$('#search-table > tbody').empty();
								
								/* Get tracks from search result. */
								currentTracks = data.result.tracks;
								
								if(typeof(currentTracks.track) == 'undefined'){
									currentTracks = [];
								}
								else if(currentTracks.track instanceof Array){
									currentTracks = currentTracks.track;
								}
								else{
									currentTracks = [currentTracks.track];
								}
								
								//currentTracks = jotify.groupIdenticalTracks(currentTracks);
								//currentTracks = jotify.sortTracks(currentTracks, 'popularity', 'asc');
								
								//$('body').prepend(prettyPrint(currentTracks));
								
								/* Loop over tracks and add them to the table. */
								for(var i in currentTracks){
									appendTrack(i, currentTracks[i]);
								}
								
								/* Trigger update event (tablesorter). */
								$('#search-table').trigger('update');
								
								if(typeof(data.result['did-you-mean']) != 'undefined'){
									info(
										'Search results for <strong>' + $('#query').val() +
										'</strong>. Did you mean <a id="did-you-mean" href="#" rel="' +
										data.result['did-you-mean'] + '">' + data.result['did-you-mean'] + '</a>?'
									);
								}
								else{
									info('Search results for <strong>' + $('#query').val() + '</strong>.');
								}
							},
							error: error
						});
						
						event.preventDefault();
					});
					
					/* Bind live click handler to tracks. */
					$('a[ id ^= "track" ]').live('click', function(event){
						var track = currentTracks[$(this).attr('rel')];
						
						queuedTracks = currentTracks.slice($(this).attr('rel') + 1);
						
						playTrack(track);
						
						event.preventDefault();
					});
					
					/* Bind live click handler to albums. */
					$('a[ id ^= "album" ]').live('click', function(event){
						var track = currentTracks[$(this).attr('rel')];
						
						$('#album-cover').attr('src', 'images/cover.png');
						
						info("Browsing... <img src=\"images/load.gif\" />");
						
						/* Browse for selected album. */
						jotify.browse({type: '2', id: track['album-id']}, session, {
							success: function(data){
								var discs = data.album.discs;
								
								if(typeof(discs.disc) == 'undefined'){
									discs = [];
								}
								else if(discs.disc instanceof Array){
									discs = discs.disc;
								}
								else{
									discs = [discs.disc];
								}
								
								$('#search-table').hide();
								$('#browse-album').show();
								
								$('#album-cover').attr('src', jotify.image(data.album.cover, session));
								$('#album-name').html(data.album.artist + ' &raquo; ' + data.album.name);
								
								/* Clear album table. */
								//$('#album-table > tbody').empty();
								$('#album-table').empty();
								
								currentTracks = [];
								
								/* Loop over discs and tracks and add them to the table. */
								for(var i in discs){
									$('#album-table').append([
										'<thead>',
										'	<tr>',
										'		<th style="font-size: 1.2em;">',
										'			<img src="images/disc.png" />',
										' 			' + (1 * i + 1) + '</th>',
										'		<th>Track</th>',
										'		<th>Time</th>',
										'		<th>Popularity</th>',
										'	</tr>',
										'</thead>',
										'<tbody id="disc-' + i +'">',
										'</tbody>'
									].join(''));
									
									for(var j in discs[i].track){
										discs[i].track[j]['album-id'] = data.album.id;
										discs[i].track[j].album       = data.album.name;
										discs[i].track[j].cover       = data.album.cover;
										
										currentTracks.push(discs[i].track[j]);
										
										appendTrackToDisc(i, currentTracks.length - 1, discs[i].track[j]);
									}
									
									$('#album-table').append('<tbody><tr><td colspan="4">&nbsp;</td></tr></tbody>');
								}
								
								info('<strong>' + data.album.name + '</strong> by <strong>' + data.album.artist + '</strong>');
							},
							error: error
						});
						
						event.preventDefault();
					});
					
					
					/* Bind controls. */
					$('#control-stop').click(function(event){
						if($('#play-on-server-checkbox').is(':checked')){
							jotify.stop(session);
						}
						else{
							audio.pause();
							audio.src = null;
						}
						
						event.preventDefault();
					});
					
					$('#control-pause').click(function(event){
						if($('#play-on-server-checkbox').is(':checked')){
							jotify.pause(session);
						}
						else{
							audio.pause();
						}
						
						event.preventDefault();
					});
					
					$('#control-play').click(function(event){
						if($('#play-on-server-checkbox').is(':checked')){
							jotify.play(session);
						}
						else{
							audio.play();
						}
						
						event.preventDefault();
					});
					
					/* Volume controller . */
					$( "#control-volume" ).slider({
						value:1,
						min: 0,
						max: 1,
						step: .1,
						slide: function( event, ui ) {
							if($('#play-on-server-checkbox').is(':checked')){
								jotify.volume(session,ui.value);
							}else{
								audio.volume = ui.value;
							}
							
						}
					});

					
					$("#password, #username").keypress(function (e) {
						if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
							$('#login').click();
						}
					});
		
					$("#query").keypress(function (e) {
						if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
							$('#search').click();
						}
					});
					
					
					
					
					
					// Submit button nice.
					$("input:submit").button();
					
				});
				
				
				
			