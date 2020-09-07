INSERT INTO oauth_client_details
	(client_id, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove)
VALUES
	("screenpostapp", /*lensdoctor789*/"$2a$10$ajfkUgZ33NIPd2EJMrg3y.mkY1pWRIOUJMtZR5OoCE.KHrlMoljSy", "read,write", "password,authorization_code,refresh_token,implicit", null, null, 43200, 864000, null, true);