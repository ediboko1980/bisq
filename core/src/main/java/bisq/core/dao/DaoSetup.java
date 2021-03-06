/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.core.dao;

import bisq.core.dao.governance.asset.AssetService;
import bisq.core.dao.governance.ballot.BallotListService;
import bisq.core.dao.governance.blindvote.BlindVoteListService;
import bisq.core.dao.governance.blindvote.MyBlindVoteListService;
import bisq.core.dao.governance.bond.reputation.BondedReputationRepository;
import bisq.core.dao.governance.bond.reputation.MyBondedReputationRepository;
import bisq.core.dao.governance.bond.reputation.MyReputationListService;
import bisq.core.dao.governance.bond.role.BondedRolesRepository;
import bisq.core.dao.governance.period.CycleService;
import bisq.core.dao.governance.proofofburn.ProofOfBurnService;
import bisq.core.dao.governance.proposal.ProposalService;
import bisq.core.dao.governance.voteresult.MissingDataRequestService;
import bisq.core.dao.governance.voteresult.VoteResultService;
import bisq.core.dao.governance.votereveal.VoteRevealService;
import bisq.core.dao.node.BsqNode;
import bisq.core.dao.node.BsqNodeProvider;
import bisq.core.dao.node.explorer.ExportJsonFilesService;
import bisq.core.dao.state.DaoStateService;

import bisq.common.handlers.ErrorMessageHandler;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * High level entry point for Dao domain.
 * We initialize all main service classes here to be sure they are started.
 */
public class DaoSetup {
    private final BsqNode bsqNode;
    private final List<DaoSetupService> daoSetupServices = new ArrayList<>();

    @Inject
    public DaoSetup(BsqNodeProvider bsqNodeProvider,
                    DaoStateService daoStateService,
                    CycleService cycleService,
                    ProposalService proposalService,
                    BallotListService ballotListService,
                    BlindVoteListService blindVoteListService,
                    MyBlindVoteListService myBlindVoteListService,
                    VoteRevealService voteRevealService,
                    VoteResultService voteResultService,
                    MissingDataRequestService missingDataRequestService,
                    BondedReputationRepository bondedReputationRepository,
                    BondedRolesRepository bondedRolesRepository,
                    MyReputationListService myReputationListService,
                    MyBondedReputationRepository myBondedReputationRepository,
                    AssetService assetService,
                    ProofOfBurnService proofOfBurnService,
                    DaoFacade daoFacade,
                    ExportJsonFilesService exportJsonFilesService) {

        bsqNode = bsqNodeProvider.getBsqNode();

        // We need to take care of order of execution.
        daoSetupServices.add(daoStateService);
        daoSetupServices.add(cycleService);
        daoSetupServices.add(proposalService);
        daoSetupServices.add(ballotListService);
        daoSetupServices.add(blindVoteListService);
        daoSetupServices.add(myBlindVoteListService);
        daoSetupServices.add(voteRevealService);
        daoSetupServices.add(voteResultService);
        daoSetupServices.add(missingDataRequestService);
        daoSetupServices.add(bondedReputationRepository);
        daoSetupServices.add(bondedRolesRepository);
        daoSetupServices.add(myReputationListService);
        daoSetupServices.add(myBondedReputationRepository);
        daoSetupServices.add(assetService);
        daoSetupServices.add(proofOfBurnService);
        daoSetupServices.add(daoFacade);
        daoSetupServices.add(exportJsonFilesService);
        daoSetupServices.add(bsqNodeProvider.getBsqNode());
    }

    public void onAllServicesInitialized(ErrorMessageHandler errorMessageHandler) {
        bsqNode.setErrorMessageHandler(errorMessageHandler);

        daoSetupServices.forEach(daoSetupServices -> {
            daoSetupServices.addListeners();
            daoSetupServices.start();
        });
    }

    public void shutDown() {
        bsqNode.shutDown();
    }
}
