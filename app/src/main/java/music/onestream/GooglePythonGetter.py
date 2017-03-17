## Python script plan:
## - use Google API
## - get (something) 

from gmusicapi import Mobileclient

api = Mobileclient()
api.login('user@gmail.com', 'my-password', Mobileclient.FROM_MAC_ADDRESS)
# => True

library = api.get_all_songs()
sweet_track_ids = [track['id'] for track in library
                   if track['artist'] == 'The Cat Empire']

playlist_id = api.create_playlist('Rad muzak')
api.add_songs_to_playlist(playlist_id, sweet_track_ids)