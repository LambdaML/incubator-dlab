/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Component, ViewChild } from '@angular/core';
import { DICTIONARY } from '../../../../dictionary/global.dictionary';

@Component({
    selector: 'cost-details-dialog',
    templateUrl: 'cost-details-dialog.component.html',
    styleUrls: ['cost-details-dialog.component.scss']
})
export class CostDetailsDialogComponent {
  readonly DICTIONARY = DICTIONARY;
  public notebook: any;

  @ViewChild('bindDialog') bindDialog;

  public open(params, notebook): void {
    this.notebook = notebook;
    this.bindDialog.open(params);
  }

  public close(): void {
    if (this.bindDialog.isOpened)
      this.bindDialog.close();
  }
}
