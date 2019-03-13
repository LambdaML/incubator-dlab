/***************************************************************************

Copyright (c) 2019, EPAM SYSTEMS INC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

****************************************************************************/

import { Injectable } from '@angular/core';

@Injectable()
export class StorageService {
  private accessTokenKey: string = 'access_token';
  private userNameKey: string = 'user_name';
  private quoteUsedKey: string = 'billing_quote';

  getToken(): string {
    return window.localStorage.getItem(this.accessTokenKey);
  }

  setAuthToken(token: string) {
    window.localStorage.setItem(this.accessTokenKey, token);
  }

  destroyToken(): void {
    window.localStorage.removeItem(this.accessTokenKey);
  }

  getUserName(): string {
    return window.localStorage.getItem(this.userNameKey);
  }

  setUserName(userName): void {
    window.localStorage.setItem(this.userNameKey, userName);
  }

  getBillingQuoteUsed(): string {
    return window.localStorage.getItem(this.quoteUsedKey);
  }

  setBillingQuoteUsed(quote): void {
    window.localStorage.setItem(this.quoteUsedKey, quote);
  }
}